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

@Service
@RequiredArgsConstructor
public class CourseService {

  private final CourseRepository courseRepo;
  private final StudySetRepository setRepo;
  private final QuestionRepository questionRepo;

  // --- Lesen & Anlegen ---

  public List<Course> list() {
    return courseRepo.findAll();
  }

  public Course get(String id) {
    return courseRepo.findById(id).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Kurs nicht gefunden"));
  }

  public List<Course> listByUser(String userId) {
    return courseRepo.findByUserId(userId);
  }

  public Page<Course> pageByUser(String userId, Pageable pageable) {
    return courseRepo.findByUserId(userId, pageable);
  }

  public Course create(Course c) {
    c.setId(null);
    return courseRepo.save(c);
  }

  public Course createForUser(String userId, Course c) {
    c.setId(null);
    c.setUserId(userId);
    return courseRepo.save(c);
  }

  public Course update(String id, Course patch) {
    Course cur = get(id);
    if (patch.getTitle() != null) cur.setTitle(patch.getTitle());
    if (patch.getDescription() != null) cur.setDescription(patch.getDescription());
    return courseRepo.save(cur);
  }

  // --- Löschen ---

  /**
   * Löscht Kurs nur, wenn er zu userId gehört.
   * Reihenfolge: Fragen -> Sets -> Kurs.
   */
  @Transactional
  public void deleteForUser(String userId, String courseId) {
    // 1) Ownership prüfen
    courseRepo.findByIdAndUserId(courseId, userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kurs nicht gefunden"));

    // 2) Alle Sets des Kurses laden
    List<StudySet> sets = setRepo.findAllByUserIdAndCourseId(userId, courseId);

    // 3) Zuerst Fragen löschen (über studySetId)
    if (!sets.isEmpty()) {
      List<String> setIds = sets.stream().map(StudySet::getId).toList();
      // ACHTUNG: QuestionRepository muss deleteAllByStudySetIdIn(Collection<String>) anbieten
      questionRepo.deleteAllByStudySetIdIn(setIds);
    }

    // 4) Sets löschen
    setRepo.deleteAllByUserIdAndCourseId(userId, courseId);

    // 5) Kurs löschen
    courseRepo.deleteByIdAndUserId(courseId, userId);
  }

  /**
   * Alt-Methode ohne userId – leitet auf deleteForUser um, damit kaskadiert wird.
   */
  @Transactional
  public void delete(String id) {
    Course c = courseRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kurs nicht gefunden"));
    deleteForUser(c.getUserId(), id);
  }
}
