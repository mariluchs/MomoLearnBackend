package com.example.momolearn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) für die Antwort bei Authentifizierungsvorgängen
 * wie Registrierung, Login oder Token-Erneuerung.
 *
 * Dieses Objekt wird vom Backend an den Client zurückgegeben.
 */
@Getter
@Setter
@NoArgsConstructor       // Erzeugt einen parameterlosen Konstruktor
@AllArgsConstructor      // Erzeugt einen Konstruktor mit allen Feldern
@Builder                 // Ermöglicht den Builder-Pattern-Aufbau für dieses Objekt
public class AuthResponse {

  /** Eindeutige ID des Benutzers. */
  private String userId;

  /** Name des Benutzers. */
  private String name;

  /** E-Mail-Adresse des Benutzers. */
  private String email;

  /** 
   * Session- oder Authentifizierungstoken (opaque Bearer-Token),
   * das vom Client für weitere Requests im "Authorization"-Header genutzt wird.
   */
  private String token;
}
