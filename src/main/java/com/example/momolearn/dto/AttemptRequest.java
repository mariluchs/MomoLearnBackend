package com.example.momolearn.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) für einen Antwort-Versuch (Attempt)
 * bei einer Frage.
 *
 * Dieses Objekt wird vom Client an das Backend geschickt,
 * z. B. bei:
 *   POST /users/{userId}/questions/{questionId}/attempts
 */
@Getter
@Setter
public class AttemptRequest {

  /**
   * Der vom Nutzer gewählte Antwortindex.
   * - @Min(0): Wert darf nicht kleiner als 0 sein
   * - @Max(3): Wert darf nicht größer als 3 sein
   *
   * Annahme:
   * Es gibt pro Frage 4 Antwortmöglichkeiten (Index 0 bis 3).
   */
  @Min(0)
  @Max(3)
  private int chosenIndex;
}
