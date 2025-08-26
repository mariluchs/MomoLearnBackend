// src/main/java/com/example/momolearn/controller/GenerationController.java
package com.example.momolearn.controller;

import com.example.momolearn.model.StudySet;
import com.example.momolearn.service.StudySetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Controller zur Integration der KI-gestützten Generierung von Fragen
 * für ein bestimmtes Lern-Set (StudySet) eines Nutzers.
 *
 * Basis-Route: /users/{userId}/sets/{setId}
 */
@RestController
@RequestMapping("/users/{userId}/sets/{setId}")
public class GenerationController {

  // Service mit der Logik zur Verwaltung und Generierung von StudySets und deren Fragen
  private final StudySetService service;

  public GenerationController(StudySetService service) {
    this.service = service;
  }

  /**
   * Startet die KI-gestützte Generierung von Fragen für das angegebene Set.
   *
   * Endpoint: POST /api/users/{userId}/sets/{setId}/generate
   *
   * Ablauf:
   *  - ruft den Service auf, um Fragen mit Hilfe der KI zu generieren
   *  - die Anzahl der zu generierenden Fragen wird von der KI bestimmt;
   *    der optionale Parameter "count" wird zwar akzeptiert, aber ignoriert
   *
   * @param userId  ID des Nutzers, dem das Set gehört
   * @param setId   ID des Sets, zu dem Fragen generiert werden sollen
   * @param count   optionaler Parameter, wird aber nicht genutzt
   * @return JSON-Objekt mit der Anzahl der erzeugten Fragen und dem Status
   *
   * Beispiel-Response:
   * {
   *   "created": 5,
   *   "status": "READY"
   * }
   */
  @PostMapping("/generate")
  public Map<String, Object> generate(@PathVariable String userId,
                                      @PathVariable String setId,
                                      @RequestParam(required = false) Integer count) {
    // Übergibt den Vorgang an den Service; die KI entscheidet intern, wie viele Fragen erstellt werden.
    int created = service.generateQuestions(userId, setId);
    return Map.of("created", created, "status", "READY");
  }

  /**
   * Gibt ein bestimmtes StudySet zurück.
   *
   * Endpoint: GET /api/users/{userId}/sets/{setId}
   *
   * Ablauf:
   *  - holt das Set aus dem Service
   *  - prüft, ob das Set wirklich zum angegebenen Nutzer gehört
   *    (Ownership-Check, um fremden Zugriff zu verhindern)
   *
   * @param userId ID des Nutzers
   * @param setId  ID des Sets
   * @return Das vollständige StudySet-Objekt
   * @throws ResponseStatusException 403, wenn das Set nicht zu diesem User gehört
   */
  @GetMapping
  public StudySet get(@PathVariable String userId, @PathVariable String setId) {
    StudySet set = service.get(setId);
    if (!userId.equals(set.getUserId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Set does not belong to user");
    }
    return set;
  }
}
