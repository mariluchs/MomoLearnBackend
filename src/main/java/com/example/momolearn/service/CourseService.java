package com.example.momolearn.service;

import com.example.momolearn.model.Course;
import com.example.momolearn.model.StudySet;
import com.example.momolearn.repository.CourseRepository;
import com.example.momolearn.repository.QuestionRepository;
import com.example.momolearn.repository.StudySetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Service-Klasse für Kurs-Operationen.
 *
 * Enthält Business-Logik für:
 * - Kurse auflisten, erstellen, bearbeiten, löschen
 * - Kaskadiertes Löschen von zugehörigen Sets und Fragen
 */
@Service
@RequiredArgsConstructor
public class CourseService {

  // Repositories für Datenzugriffe
  private final CourseRepository courseRepo;
  private final StudySetRepository setRepo;
  private final QuestionRepository questionRepo;

  // ------------------------------------------------------------
  // --- Lesen & Anlegen ---
  // ------------------------------------------------------------

  /** Gibt alle Kurse im System zurück (ohne Filter). */
  public List<Course> list() {
    return courseRepo.findAll();
  }

  /** Holt einen Kurs anhand seiner ID oder wirft 404, wenn er nicht existiert. */
  public Course get(String id) {
    return courseRepo.findById(id)
        .orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Kurs nicht gefunden"));
  }

  /** Holt alle Kurse, die einem bestimmten Benutzer gehören. */
  public List<Course> listByUser(String userId) {
    return courseRepo.findByUserId(userId);
  }

  /** Holt alle Kurse eines Benutzers, paginiert. */
  public Page<Course> pageByUser(String userId, Pageable pageable) {
    return courseRepo.findByUserId(userId, pageable);
  }

  /** Erstellt einen Kurs ohne Benutzerbindung. (Wird selten genutzt.) */
  public Course create(Course c) {
    c.setId(null); // Sicherstellen, dass MongoDB eine neue ID generiert
    return courseRepo.save(c);
  }

  /** Erstellt einen Kurs für einen bestimmten Benutzer. */
  public Course createForUser(String userId, Course c) {
    c.setId(null);
    c.setUserId(userId);
    return courseRepo.save(c);
  }

  /** Aktualisiert einen Kurs teilweise (nur Felder, die gesetzt sind). */
  public Course update(String id, Course patch) {
    Course cur = get(id);
    if (patch.getTitle() != null) cur.setTitle(patch.getTitle());
    if (patch.getDescription() != null) cur.setDescription(patch.getDescription());
    return courseRepo.save(cur);
  }

  // ------------------------------------------------------------
  // --- Löschen ---
  // ------------------------------------------------------------

  /**
   * Löscht einen Kurs samt aller zugehörigen Sets und Fragen.
   *
   * Ablauf:
   * 1. Ownership prüfen (Kurs gehört diesem Benutzer)
   * 2. Alle StudySets des Kurses laden
   * 3. Fragen in diesen Sets löschen
   * 4. Sets löschen
   * 5. Kurs selbst löschen
   */
  @Transactional
  public void deleteForUser(String userId, String courseId) {
    // 1) Prüfen, ob Kurs dem Benutzer gehört
    courseRepo.findByIdAndUserId(courseId, userId)
        .orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Kurs nicht gefunden"));

    // 2) Alle StudySets des Kurses laden
    List<StudySet> sets = setRepo.findAllByUserIdAndCourseId(userId, courseId);

    // 3) Zuerst Fragen löschen, um "hängende" Daten zu vermeiden
    if (!sets.isEmpty()) {
      List<String> setIds = sets.stream().map(StudySet::getId).toList();
      questionRepo.deleteAllByStudySetIdIn(setIds);
    }

    // 4) Alle StudySets löschen
    setRepo.deleteAllByUserIdAndCourseId(userId, courseId);

    // 5) Kurs selbst löschen
    courseRepo.deleteByIdAndUserId(courseId, userId);
  }

  /**
   * Löscht einen Kurs ohne explizite Benutzer-ID.
   * Ermittelt den Besitzer und ruft dann `deleteForUser` auf,
   * um kaskadiert alle zugehörigen Daten zu entfernen.
   */
  @Transactional
  public void delete(String id) {
    Course c = courseRepo.findById(id)
        .orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Kurs nicht gefunden"));
    deleteForUser(c.getUserId(), id);
  }
}
