package com.example.momolearn.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * MongoDB-Dokument, das einen Benutzer repräsentiert.
 *
 * Gespeichert in der Collection "users".
 */
@Document("users")
@Getter
@Setter
@NoArgsConstructor       // Standard-Konstruktor
@AllArgsConstructor      // Konstruktor mit allen Feldern
@Builder                 // Builder-Pattern für einfaches Erstellen
public class User {

  /** Eindeutige ID des Benutzers, wird von MongoDB generiert. */
  @Id
  private String id;

  /** Name des Benutzers. Darf nicht leer sein. */
  @NotBlank
  private String name;

  /**
   * E-Mail-Adresse des Benutzers.
   * - @Email: Validiert korrektes E-Mail-Format.
   * - @Indexed(unique = true): Muss einzigartig sein.
   */
  @Email
  @Indexed(unique = true)
  private String email;

  /** Zeitpunkt, an dem das Benutzerkonto erstellt wurde. */
  private Instant createdAt;

  /**
   * Passwort-Hash (BCrypt), nie das Klartext-Passwort!
   * Mit @JsonIgnore versehen, um zu verhindern, dass der Hash in API-Responses ausgegeben wird.
   */
  @JsonIgnore
  private String passwordHash;

  // --- Gamification-Daten ---

  /** Aktuelle Erfahrungspunkte des Benutzers (XP). */
  @Builder.Default
  private int xp = 0;

  /** Aktuelles Level des Benutzers. */
  @Builder.Default
  private int level = 1;

  /** Aktuelle Streak (z. B. Tage in Folge aktiv oder richtige Antworten in Folge). */
  @Builder.Default
  private int streak = 0;

  /** Zeitpunkt des letzten beantworteten Versuchs, für Streak-Berechnung. */
  private Instant lastAnswerAt;
}
