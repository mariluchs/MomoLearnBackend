package com.example.momolearn.controller;

import com.example.momolearn.model.StudySet;
import com.example.momolearn.service.StudySetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * REST-Controller für das Verwalten von StudySets (Lern-Sets) innerhalb eines bestimmten Kurses
 * eines bestimmten Benutzers.
 *
 * Basisroute:
 *   /users/{userId}/courses/{courseId}/sets
 *
 * Hier werden alle CRUD-Operationen für StudySets angeboten.
 */
@RestController
@RequestMapping("/users/{userId}/courses/{courseId}/sets")
public class StudySetController {

  // Service, der die Geschäftslogik für StudySets enthält
  private final StudySetService service;

  // Konstruktor-Injektion (sauberer, einfacher zu testen)
  public StudySetController(StudySetService service) { 
    this.service = service; 
  }

  /**
   * Erstellt ein neues StudySet in einem Kurs.
   *
   * POST /users/{userId}/courses/{courseId}/sets
   *
   * @param userId   ID des Benutzers
   * @param courseId ID des Kurses
   * @param body     JSON-Body mit mindestens:
   *                 - "title": Titel des Sets
   *                 - "uploadId": optional, falls ein Upload referenziert wird
   * @return das erstellte StudySet
   *
   * Gibt bei Erfolg HTTP 201 Created zurück.
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public StudySet create(@PathVariable String userId,
                         @PathVariable String courseId,
                         @RequestBody Map<String,String> body) {
    return service.create(userId, courseId, body.get("title"), body.get("uploadId"));
  }

  /**
   * Liefert eine paginierte Liste aller StudySets eines Nutzers in einem bestimmten Kurs.
   *
   * GET /users/{userId}/courses/{courseId}/sets?page=0&size=10
   *
   * @param userId   ID des Benutzers
   * @param courseId ID des Kurses
   * @param pageable automatisch von Spring befüllt (page, size, sort)
   * @return Page mit StudySets
   */
  @GetMapping
  public Page<StudySet> list(@PathVariable String userId,
                             @PathVariable String courseId,
                             Pageable pageable) {
    return service.pageByUserAndCourse(userId, courseId, pageable);
  }

  /**
   * Holt ein bestimmtes StudySet eines Nutzers in einem bestimmten Kurs.
   *
   * GET /users/{userId}/courses/{courseId}/sets/{setId}
   *
   * @return das StudySet-Objekt
   * @throws org.springframework.web.server.ResponseStatusException 404, wenn nicht gefunden
   */
  @GetMapping("/{setId}")
  public StudySet get(@PathVariable String userId,
                      @PathVariable String courseId,
                      @PathVariable String setId) {
    return service.getForUserCourse(userId, courseId, setId);
  }

  /**
   * Löscht ein bestimmtes StudySet.
   *
   * DELETE /users/{userId}/courses/{courseId}/sets/{setId}
   *
   * @return HTTP 204 No Content bei Erfolg
   */
  @DeleteMapping("/{setId}")
  public ResponseEntity<Void> delete(@PathVariable String userId,
                                     @PathVariable String courseId,
                                     @PathVariable String setId) {
    service.delete(userId, courseId, setId);
    return ResponseEntity.noContent().build();
  }
}
