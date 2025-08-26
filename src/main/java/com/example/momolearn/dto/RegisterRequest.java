package com.example.momolearn.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) für Registrierungs-Anfragen.
 *
 * Dieses Objekt wird vom Client beim Registrieren eines neuen Benutzers
 * an das Backend geschickt, z. B. an:
 *   POST /auth/register
 */
@Getter
@Setter
public class RegisterRequest {

  /**
   * Name des neuen Benutzers.
   * - @NotBlank: Darf nicht leer oder nur aus Leerzeichen bestehen.
   */
  @NotBlank
  private String name;

  /**
   * E-Mail-Adresse des Benutzers.
   * - @Email: Muss im Format einer gültigen E-Mail sein.
   * - @NotBlank: Darf nicht leer sein.
   */
  @Email
  @NotBlank
  private String email;

  /**
   * Passwort für das neue Benutzerkonto.
   * - @NotBlank: Darf nicht leer sein.
   */
  @NotBlank
  private String password;
}
