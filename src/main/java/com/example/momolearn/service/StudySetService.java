package com.example.momolearn.service;

import com.example.momolearn.model.*;
import com.example.momolearn.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;

/**
 * Service zur Verwaltung von StudySets und Integration mit der KI-Generierung.
 *
 * Zuständig für:
 * - Erstellen, Abrufen, Löschen und Auflisten von StudySets
 * - Starten des KI-Workflows: PDF → Text → Fragen generieren → Speichern
 */
@Service
public class StudySetService {

  private final StudySetRepository sets;
  private final CourseRepository courses;
  private final UploadRepository uploads;
  private final UploadService uploadService;
  private final PdfTextService pdfText;
  private final AiQuestionGenerator generator;   // KI-Client für DeepSeek
  private final QuestionRepository questions;

  public StudySetService(
      StudySetRepository sets,
      CourseRepository courses,
      UploadRepository uploads,
      UploadService uploadService,
      PdfTextService pdfText,
      AiQuestionGenerator generator,
      QuestionRepository questions
  ) {
    this.sets = sets;
    this.courses = courses;
    this.uploads = uploads;
    this.uploadService = uploadService;
    this.pdfText = pdfText;
    this.generator = generator;
    this.questions = questions;
  }

  // ------------------------------------------------------------------------
  // CRUD-Funktionen für StudySets
  // ------------------------------------------------------------------------

  /**
   * Erstellt ein neues StudySet.
   * - Prüft, ob der Kurs existiert und dem Nutzer gehört.
   * - Prüft optional, ob der Upload (PDF) existiert und dem Nutzer gehört.
   * - Legt das Set mit Status PENDING an.
   */
  public StudySet create(String userId, String courseId, String title, String uploadId) {
    Course course = courses.findById(courseId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    if (!userId.equals(course.getUserId()))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Course does not belong to user");

    if (title == null || title.isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title required");

    if (uploadId != null) {
      UploadDoc up = uploads.findById(uploadId)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload not found"));
      if (!userId.equals(up.getUserId()))
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Upload does not belong to user");
    }

    StudySet set = StudySet.builder()
        .userId(userId)
        .courseId(courseId)
        .title(title.trim())
        .uploadId(uploadId)
        .status(StudySet.Status.PENDING) // Status ist zunächst "wartend"
        .createdAt(Instant.now())
        .build();

    return sets.save(set);
  }

  /** Holt ein StudySet anhand seiner ID oder wirft 404. */
  public StudySet get(String id) {
    return sets.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "StudySet not found"));
  }

  /** Gibt alle Sets eines Nutzers in einem bestimmten Kurs zurück. */
  public List<StudySet> list(String userId, String courseId) {
    return sets.findAllByUserIdAndCourseId(userId, courseId);
  }

  /** Holt ein bestimmtes Set eines Nutzers innerhalb eines Kurses. */
  public StudySet getForUserCourse(String userId, String courseId, String setId) {
    return sets.findByIdAndUserIdAndCourseId(setId, userId, courseId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Set nicht gefunden"));
  }

  /** Alternative Methode für Liste nach Benutzer und Kurs. */
  public List<StudySet> listByUserAndCourse(String userId, String courseId) {
    return sets.findByUserIdAndCourseId(userId, courseId);
  }

  /** Löscht ein Set, wenn es dem Nutzer gehört. */
  public void delete(String userId, String courseId, String setId) {
    StudySet s = getForUserCourse(userId, courseId, setId);
    sets.deleteById(s.getId());
  }

  /** Gibt alle Sets eines Nutzers in einem Kurs paginiert zurück. */
  public Page<StudySet> pageByUserAndCourse(String userId, String courseId, Pageable pageable) {
    return sets.findByUserIdAndCourseId(userId, courseId, pageable);
  }

  // ------------------------------------------------------------------------
  // KI-Integration
  // ------------------------------------------------------------------------

  /**
   * Startet den KI-Workflow, um automatisch Fragen aus einem PDF zu generieren.
   *
   * Ablauf:
   * 1. Ownership des Sets und Uploads prüfen.
   * 2. PDF aus GridFS öffnen.
   * 3. Text extrahieren.
   * 4. Bisherige Fragen löschen (falls schon vorhanden).
   * 5. KI-Aufruf starten (DeepSeek) → Fragen erzeugen.
   * 6. Fragen speichern, Status auf READY setzen.
   *
   * @return Anzahl der generierten Fragen
   */
  public int generateQuestions(String userId, String setId) {
    // 1) Ownership-Check
    StudySet set = get(setId);
    if (!userId.equals(set.getUserId()))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Set does not belong to user");

    UploadDoc up = uploads.findById(set.getUploadId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload not found"));
    if (!userId.equals(up.getUserId()))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Upload does not belong to user");

    try (InputStream in = uploadService.openStream(up)) {
      // 2) PDF → Text
      String text = pdfText.extractText(in);

      // 3) Alte Fragen löschen, falls Set neu generiert wird
      questions.deleteByStudySetId(set.getId());

      // 4) KI ansprechen → Fragen generieren
      var generated = generator.generateDecideCount(set.getId(), text);

      // 5) Generierte Fragen speichern
      questions.saveAll(generated);

      // 6) StudySet auf READY setzen
      set.setStatus(StudySet.Status.READY);
      sets.save(set);

      return generated.size();
    } catch (Exception e) {
      // Bei Fehler: Set auf FAILED setzen und Fehler weiterwerfen
      set.setStatus(StudySet.Status.FAILED);
      sets.save(set);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "Generation failed: " + e.getMessage()
      );
    }
  }
}
