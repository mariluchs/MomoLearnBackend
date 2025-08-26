package com.example.momolearn.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) für Login-Anfragen.
 *
 * Dieses Objekt wird vom Client an das Backend geschickt,
 * z. B. bei einem POST-Request an:
 *   POST /auth/login
 */
@Getter
@Setter
public class LoginRequest {

  /**
   * E-Mail-Adresse des Benutzers.
   * - @Email: Validiert, dass es sich um eine gültige E-Mail handelt.
   * - @NotBlank: Darf nicht leer oder nur aus Leerzeichen bestehen.
   */
  @Email
  @NotBlank
  private String email;

  /**
   * Passwort des Benutzers.
   * - @NotBlank: Darf nicht leer sein.
   */
  @NotBlank
  private String password;
}
