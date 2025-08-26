package com.example.momolearn.dto;

import lombok.*;

/**
 * DTO (Data Transfer Object) für das Ergebnis eines Antwort-Versuchs.
 *
 * Wird vom Backend zurückgegeben, nachdem ein Nutzer eine Frage beantwortet hat.
 * Typischer Endpunkt:
 *   POST /users/{userId}/questions/{questionId}/attempts
 */
@Getter
@Setter
@NoArgsConstructor       // Erzeugt einen parameterlosen Konstruktor
@AllArgsConstructor      // Erzeugt einen Konstruktor mit allen Feldern
@Builder                 // Erlaubt den Builder-Pattern-Aufbau für dieses Objekt
public class AttemptResultDto {

  /** 
   * Gibt an, ob der gegebene Versuch korrekt war.
   */
  private boolean correct;

  /**
   * XP, die der Nutzer für diesen Versuch erhält.
   */
  private int xpAwarded;

  /**
   * Gesamt-XP des Nutzers nach diesem Versuch.
   */
  private int newUserXp;

  /**
   * Neues Level des Nutzers nach diesem Versuch.
   */
  private int newUserLevel;

  /**
   * Aktuelle Streak des Nutzers (z. B. aufeinanderfolgende richtige Antworten).
   */
  private int streak;
}
