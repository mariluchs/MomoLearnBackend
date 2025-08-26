package com.example.momolearn.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB-Dokument zur Speicherung einzelner Antwort-Versuche eines Nutzers.
 *
 * Wird in der Collection "answer_attempts" gespeichert.
 */
@Document("answer_attempts")
@Getter
@Setter
@NoArgsConstructor       // Parameterloser Konstruktor
@AllArgsConstructor      // Konstruktor mit allen Feldern
@Builder                 // Builder-Pattern für einfaches Erstellen von Objekten
public class AnswerAttempt {

  /** Eindeutige ID des Antwort-Versuchs (generiert von MongoDB). */
  @Id
  private String id;

  /** ID des Nutzers, der die Antwort gegeben hat. */
  private String userId;

  /** ID der Frage, zu der der Versuch gehört. */
  private String questionId;

  /** Index der gewählten Antwortoption (z. B. 0–3). */
  private int chosenIndex;

  /** Ob der Versuch korrekt war (true) oder nicht (false). */
  private boolean correct;

  /** XP, die der Nutzer für diesen Versuch erhalten hat. */
  private int scoredXp;

  /** Zeitpunkt, wann der Versuch erstellt wurde. */
  private Instant createdAt;
}
