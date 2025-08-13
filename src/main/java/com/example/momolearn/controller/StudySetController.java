package com.example.momolearn.controller;

import com.example.momolearn.model.StudySet;
import com.example.momolearn.service.StudySetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

@RestController
@RequestMapping("/users/{userId}/courses/{courseId}/sets")
public class StudySetController {

  private final StudySetService service;
  public StudySetController(StudySetService service) { this.service = service; }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public StudySet create(@PathVariable String userId,
                         @PathVariable String courseId,
                         @RequestBody Map<String,String> body) {
    return service.create(userId, courseId, body.get("title"), body.get("uploadId"));
  }

  @GetMapping
    public Page<StudySet> list(@PathVariable String userId,
                           @PathVariable String courseId,
                           Pageable pageable) {
        return service.pageByUserAndCourse(userId, courseId, pageable);
    }

  @GetMapping("/{setId}")
  public StudySet get(
      @PathVariable String userId,
      @PathVariable String courseId,
      @PathVariable String setId) {
    return service.getForUserCourse(userId, courseId, setId);
  }

  @DeleteMapping("/{setId}")
  public ResponseEntity<Void> delete(
      @PathVariable String userId,
      @PathVariable String courseId,
      @PathVariable String setId) {
    service.delete(userId, courseId, setId);
    return ResponseEntity.noContent().build(); // 204
  }
}
