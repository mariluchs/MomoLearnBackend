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

@Service
public class StudySetService {

  private final StudySetRepository sets;
  private final CourseRepository courses;
  private final UploadRepository uploads;
  private final UploadService uploadService;
  private final PdfTextService pdfText;
  private final QuestionGenerator generator;
  private final QuestionRepository questions;

  public StudySetService(StudySetRepository sets, CourseRepository courses,
                         UploadRepository uploads, UploadService uploadService,
                         PdfTextService pdfText, QuestionGenerator generator,
                         QuestionRepository questions) {
    this.sets = sets;
    this.courses = courses;
    this.uploads = uploads;
    this.uploadService = uploadService;
    this.pdfText = pdfText;
    this.generator = generator;
    this.questions = questions;
  }

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
        .status(StudySet.Status.PENDING)
        .createdAt(Instant.now())
        .build();

    return sets.save(set);
  }

  public StudySet get(String id) {
    return sets.findById(id).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "StudySet not found"));
  }

   public List<StudySet> list(String userId, String courseId) {
    return sets.findAllByUserIdAndCourseId(userId, courseId);
  }

  public StudySet getForUserCourse(String userId, String courseId, String setId) {
    return sets.findByIdAndUserIdAndCourseId(setId, userId, courseId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Set nicht gefunden"));
  }

  public List<StudySet> listByUserAndCourse(String userId, String courseId) {
    return sets.findByUserIdAndCourseId(userId, courseId);
  }

  public void delete(String userId, String courseId, String setId) {
    // 404, wenn nicht vorhanden oder nicht zu User/Course gehörend
    StudySet s = getForUserCourse(userId, courseId, setId);
    sets.deleteById(s.getId());
  }

  public Page<StudySet> pageByUserAndCourse(String userId, String courseId, Pageable pageable) {
    return sets.findByUserIdAndCourseId(userId, courseId, pageable);
}

  public int generateQuestions(String userId, String setId, int count) {
    StudySet set = get(setId);
    if (!userId.equals(set.getUserId()))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Set does not belong to user");

    UploadDoc up = uploads.findById(set.getUploadId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload not found"));
    if (!userId.equals(up.getUserId()))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Upload does not belong to user");

    try (InputStream in = uploadService.openStream(up)) {
      String text = pdfText.extractText(in);
      questions.deleteByStudySetId(setId); // alte Fragen (falls vorhanden) überschreiben
      var qs = generator.generate(setId, text, Math.max(1, count));
      questions.saveAll(qs);
      set.setStatus(StudySet.Status.READY);
      sets.save(set);
      return qs.size();
    } catch (Exception e) {
      set.setStatus(StudySet.Status.FAILED);
      sets.save(set);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Generation failed: " + e.getMessage());
    }
  }
}
