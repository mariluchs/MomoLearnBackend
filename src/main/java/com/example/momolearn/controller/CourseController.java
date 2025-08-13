package com.example.momolearn.controller;

import com.example.momolearn.model.Course;
import com.example.momolearn.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/courses") // /api via application.properties
public class CourseController {

  private final CourseService courseService;

  @GetMapping
  public List<Course> list(@PathVariable String userId) {
    return courseService.listByUser(userId);
  }

  @GetMapping(params = {"page", "size"})
  public Page<Course> page(@PathVariable String userId, Pageable pageable) {
    return courseService.pageByUser(userId, pageable);
  }

  @GetMapping("/{courseId}")
  public Course getOne(@PathVariable String userId, @PathVariable String courseId) {
    Course c = courseService.get(courseId);
    if (c == null || !userId.equals(c.getUserId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kurs nicht gefunden");
    }
    return c;
  }

  @PostMapping
  public ResponseEntity<Course> create(@PathVariable String userId, @RequestBody Course c) {
    Course saved = courseService.createForUser(userId, c);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
  }

  @PutMapping("/{courseId}")
  public Course update(@PathVariable String userId,
                       @PathVariable String courseId,
                       @RequestBody Course patch) {
    Course existing = courseService.get(courseId);
    if (existing == null || !userId.equals(existing.getUserId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kurs nicht gefunden");
    }
    return courseService.update(courseId, patch);
  }

  @DeleteMapping("/{courseId}")
  public ResponseEntity<Void> delete(@PathVariable String userId, @PathVariable String courseId) {
    courseService.deleteForUser(userId, courseId);
    return ResponseEntity.noContent().build();
  }
}
