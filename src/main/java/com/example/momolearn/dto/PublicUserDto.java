package com.example.momolearn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) für die öffentliche Darstellung eines Benutzers.
 *
 * Dieses Objekt wird verwendet, wenn das Backend
 * nur "sichere" Benutzerinformationen an den Client zurückgeben soll,
 * z. B. bei:
 *   - /auth/me
 *   - User-Infos in Listen oder Profilübersichten
 */
@Getter
@Setter
@NoArgsConstructor       // Erstellt einen leeren Standard-Konstruktor
@AllArgsConstructor      // Erstellt einen Konstruktor mit allen Feldern
@Builder                 // Ermöglicht den Aufbau über das Builder-Pattern
public class PublicUserDto {

  /** Eindeutige ID des Benutzers. */
  private String id;

  /** Name des Benutzers. */
  private String name;

  /** E-Mail-Adresse des Benutzers. */
  private String email;
}
