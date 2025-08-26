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

/**
 * REST-Controller für die Verwaltung von Kursen eines bestimmten Users.
 * Basisroute: /users/{userId}/courses   (Prefix /api kann global über application.properties gesetzt sein)
 */
@RestController
@RequiredArgsConstructor // erzeugt einen Konstruktor für finale Felder (Dependency Injection)
@RequestMapping("/users/{userId}/courses")
public class CourseController {

  // Geschäftslogik liegt im Service; Controller bleibt schlank
  private final CourseService courseService;

  /**
   * Liste alle Kurse eines Nutzers (ohne Pagination).
   * GET /users/{userId}/courses
   */
  @GetMapping
  public List<Course> list(@PathVariable String userId) {
    return courseService.listByUser(userId);
  }

  /**
   * Paginierte Liste der Kurse eines Nutzers.
   * Wird aufgerufen, wenn Query-Parameter page und size vorhanden sind.
   * Beispiel: GET /users/{userId}/courses?page=0&size=20
   */
  @GetMapping(params = {"page", "size"})
  public Page<Course> page(@PathVariable String userId, Pageable pageable) {
    return courseService.pageByUser(userId, pageable);
  }

  /**
   * Hole einen konkreten Kurs eines Nutzers.
   * GET /users/{userId}/courses/{courseId}
   * 
   * Sicherheits-/Konsistenz-Check: Der gefundene Kurs muss dem userId gehören,
   * sonst wird 404 NOT FOUND geworfen (verhindert Fremdzugriff).
   */
  @GetMapping("/{courseId}")
  public Course getOne(@PathVariable String userId, @PathVariable String courseId) {
    Course c = courseService.get(courseId);
    if (c == null || !userId.equals(c.getUserId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kurs nicht gefunden");
    }
    return c;
  }

  /**
   * Erstelle einen neuen Kurs für den angegebenen Nutzer.
   * POST /users/{userId}/courses
   * 
   * Rückgabe: 201 CREATED + der gespeicherte Kurs im Body.
   */
  @PostMapping
  public ResponseEntity<Course> create(@PathVariable String userId, @RequestBody Course c) {
    Course saved = courseService.createForUser(userId, c);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
  }

  /**
   * Aktualisiere einen Kurs (vollständig oder als Patch-Objekt genutzt).
   * PUT /users/{userId}/courses/{courseId}
   * 
   * Vor dem Update wird geprüft, ob der Kurs existiert und dem userId gehört.
   * Andernfalls 404 NOT FOUND.
   */
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

  /**
   * Lösche einen Kurs des Nutzers.
   * DELETE /users/{userId}/courses/{courseId}
   * 
   * Der Service kümmert sich darum, nur Kurse dieses Users zu entfernen
   * (oder intern eine NOT FOUND/Forbidden-Logik auszulösen).
   * Rückgabe: 204 NO CONTENT ohne Body.
   */
  @DeleteMapping("/{courseId}")
  public ResponseEntity<Void> delete(@PathVariable String userId, @PathVariable String courseId) {
    courseService.deleteForUser(userId, courseId);
    return ResponseEntity.noContent().build();
  }
}
