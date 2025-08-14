// src/main/java/com/example/momolearn/controller/GenerationController.java
package com.example.momolearn.controller;

import com.example.momolearn.model.StudySet;
import com.example.momolearn.service.StudySetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/users/{userId}/sets/{setId}")
public class GenerationController {

  private final StudySetService service;

  public GenerationController(StudySetService service) {
    this.service = service;
  }

  /**
   * Startet die KI-Generierung. Die KI entscheidet selbst, wie viele Fragen sinnvoll sind.
   * Ein optional übergebener "count" wird aus Kompatibilitätsgründen akzeptiert, aber ignoriert.
   *
   * POST /api/users/{userId}/sets/{setId}/generate
   */
  @PostMapping("/generate")
  public Map<String, Object> generate(@PathVariable String userId,
                                      @PathVariable String setId,
                                      @RequestParam(required = false) Integer count) {
    int created = service.generateQuestions(userId, setId); // <-- ohne count
    return Map.of("created", created, "status", "READY");
  }

  /**
   * Liefert das Set (mit Ownership-Check).
   *
   * GET /api/users/{userId}/sets/{setId}
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
