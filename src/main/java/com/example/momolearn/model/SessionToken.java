package com.example.momolearn.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * MongoDB-Dokument, das ein Session-Token für die Authentifizierung speichert.
 *
 * Diese Tokens werden beim Login oder bei der Registrierung erstellt
 * und für authentifizierte API-Aufrufe als Bearer-Token genutzt.
 */
@Document("session_tokens")
@Getter
@Setter
@NoArgsConstructor       // Standard-Konstruktor
@AllArgsConstructor      // Konstruktor mit allen Feldern
@Builder                 // Builder-Pattern für bequemen Objektaufbau
public class SessionToken {

  /** Eindeutige ID des Tokens (wird von MongoDB generiert). */
  @Id
  private String id;

  /** 
   * Der eigentliche Token-String, der an den Client ausgegeben wird.
   * Ist eindeutig und in der Datenbank unique indiziert.
   */
  @Indexed(unique = true)
  private String token;

  /** 
   * ID des Benutzers, zu dem dieser Token gehört.
   * Indexed für schnelle Suche nach allen Tokens eines Nutzers.
   */
  @Indexed
  private String userId;

  /** Zeitpunkt, wann der Token erstellt wurde. */
  private Instant createdAt;

  /** Ablaufdatum des Tokens (z. B. 7 Tage nach Erstellung). */
  private Instant expiresAt;
}
