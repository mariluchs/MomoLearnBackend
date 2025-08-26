package com.example.momolearn.dto;

import lombok.*;

/**
 * DTO (Data Transfer Object) für die Gamification-Statistiken eines Benutzers.
 *
 * Wird typischerweise als Antwort an den Client zurückgegeben, wenn
 * der Endpunkt:
 *   GET /users/{userId}/stats
 * aufgerufen wird.
 */
@Getter
@Setter
@NoArgsConstructor       // Standard-Konstruktor
@AllArgsConstructor      // Konstruktor mit allen Feldern
@Builder                 // Builder-Pattern für einfachen, sauberen Objektaufbau
public class UserStatsDto {

  /**
   * Aktuelle Gesamt-XP des Nutzers.
   */
  private int xp;

  /**
   * Aktuelles Level des Nutzers.
   */
  private int level;

  /**
   * Aktuelle Streak des Nutzers (z. B. wie viele Tage in Folge aktiv oder
   * wie viele richtige Antworten hintereinander).
   */
  private int streak;

  /**
   * Gesamtanzahl korrekt beantworteter Fragen.
   */
  private long correctAnswers;
}
