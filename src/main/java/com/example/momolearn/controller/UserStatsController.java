package com.example.momolearn.controller;

import com.example.momolearn.dto.UserStatsDto;
import com.example.momolearn.service.GamificationService;
import org.springframework.web.bind.annotation.*;

/**
 * REST-Controller für die Abfrage der Gamification-Statistiken eines Benutzers.
 *
 * Basis-Route:
 *   /users/{userId}/stats
 */
@RestController
@RequestMapping("/users/{userId}/stats")
public class UserStatsController {

  // Service, der die Geschäftslogik zur Berechnung und Bereitstellung von User-Statistiken enthält
  private final GamificationService service;

  // Konstruktor-Injektion für den Service
  public UserStatsController(GamificationService service) {
    this.service = service;
  }

  /**
   * Ruft die Gamification-Statistiken eines Benutzers ab.
   *
   * GET /users/{userId}/stats
   *
   * Ablauf:
   * 1. Übergibt die Benutzer-ID an den GamificationService.
   * 2. Dieser Service berechnet oder sammelt die Statistiken des Nutzers.
   * 3. Gibt ein `UserStatsDto`-Objekt zurück, das die relevanten Daten enthält.
   *
   * @param userId ID des Nutzers (aus der URL)
   * @return ein DTO mit Statistiken wie XP, Level, Streaks etc.
   */
  @GetMapping
  public UserStatsDto get(@PathVariable String userId) {
    return service.stats(userId);
  }
}
