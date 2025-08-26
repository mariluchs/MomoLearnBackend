package com.example.momolearn.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.momolearn.dto.AuthResponse;
import com.example.momolearn.dto.LoginRequest;
import com.example.momolearn.dto.PublicUserDto;
import com.example.momolearn.dto.RegisterRequest;
import com.example.momolearn.model.SessionToken;
import com.example.momolearn.model.User;
import com.example.momolearn.repository.SessionTokenRepository;
import com.example.momolearn.repository.UserRepository;

import jakarta.validation.Valid;

/**
 * REST-Controller für Authentifizierung und Session-Handling.
 * Stellt Endpunkte für Registrierung, Login, Logout und "Wer bin ich?" bereit.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

  // Repositories für Benutzer und Session-Tokens (persistente Sitzungen)
  private final UserRepository users;
  private final SessionTokenRepository tokens;

  // Konstruktor-Injektion (empfohlen: immutable Dependencies, bessere Testbarkeit)
  public AuthController(UserRepository users, SessionTokenRepository tokens) {
    this.users = users;
    this.tokens = tokens;
  }

  /**
   * Registrierung eines neuen Nutzers.
   * - prüft, ob E-Mail bereits existiert
   * - hashed das Passwort mit BCrypt
   * - legt den User an und erstellt direkt ein Session-Token (Auto-Login)
   * 
   * @return AuthResponse inkl. frischem Token
   */
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED) // 201 Created bei erfolgreicher Registrierung
  public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
    // Wenn E-Mail schon vergeben -> 409 CONFLICT
    users.findByEmail(req.getEmail()).ifPresent(u -> {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "E-Mail bereits vergeben");
    });

    // Passwort sicher hashen (mit Salt, das BCrypt intern generiert)
    String hash = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt());

    // Benutzer anlegen (Startwerte für Gamification: xp/level/streak)
    User u = users.save(User.builder()
        .name(req.getName().trim())
        .email(req.getEmail().trim().toLowerCase())
        .passwordHash(hash)
        .createdAt(Instant.now())
        .xp(0).level(1).streak(0)
        .build());

    // Session-Token (UUID) ausstellen und persistieren
    String token = issueToken(u.getId());

    // Antwortobjekt für das Frontend (keine sensiblen Daten wie passwordHash!)
    return AuthResponse.builder()
        .userId(u.getId())
        .name(u.getName())
        .email(u.getEmail())
        .token(token)
        .build();
  }

  /**
   * Login mit E-Mail und Passwort.
   * - sucht User via E-Mail
   * - verifiziert Passwort mit BCrypt
   * - erstellt ein neues Session-Token
   */
  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest req) {
    // User lookup (E-Mail normalisiert)
    User u = users.findByEmail(req.getEmail().trim().toLowerCase())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ungültige Anmeldedaten"));

    // Passwortvergleich (konstantzeitnahe Prüfung durch BCrypt)
    if (u.getPasswordHash() == null || !BCrypt.checkpw(req.getPassword(), u.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ungültige Anmeldedaten");
    }

    // Bei Erfolg: neues Session-Token ausstellen
    String token = issueToken(u.getId());

    // Minimale Benutzerinfo + Token zurückgeben
    return AuthResponse.builder()
        .userId(u.getId())
        .name(u.getName())
        .email(u.getEmail())
        .token(token)
        .build();
  }

  /**
   * Logout.
   * - liest das Bearer-Token aus dem Authorization-Header
   * - löscht den zugehörigen Token-Datensatz (Session invalidieren)
   * - gibt 204 No Content zurück
   */
  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(@RequestHeader(value = "Authorization", required = false) String auth) {
    String token = parseBearer(auth);
    if (token != null) tokens.deleteByToken(token);
  }

  /**
   * Eigene Nutzerdaten abfragen (leichtgewichtige "whoami"-Funktion).
   * - parst Bearer-Token
   * - lädt SessionToken und danach den User
   * - gibt eine PublicUserDto ohne sensible Felder zurück
   */
  @GetMapping("/me")
  public PublicUserDto me(@RequestHeader(value = "Authorization", required = false) String auth) {
    String token = parseBearer(auth);

    // Session prüfen (wirft 401, wenn kein/ungültiger Token)
    var st = tokens.findByToken(token)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Kein gültiger Token"));

    // Zugehörigen Benutzer ermitteln; wenn nicht vorhanden -> 401
    return users.findById(st.getUserId())
        .map(u -> PublicUserDto.builder().id(u.getId()).name(u.getName()).email(u.getEmail()).build())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Benutzer unbekannt"));
  }

  // --- Helpers ---

  /**
   * Erstellt ein neues Session-Token (UUID), speichert es mit Ablaufdatum (+7 Tage)
   * und gibt den reinen Token-String zurück.
   */
  private String issueToken(String userId) {
    String tok = UUID.randomUUID().toString(); // zufälliger, eindeutiger Tokenwert
    tokens.save(SessionToken.builder()
        .token(tok)
        .userId(userId)
        .createdAt(Instant.now())
        .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS)) // Ablaufzeitpunkt setzen
        .build());
    return tok;
  }

  /**
   * Extrahiert einen Bearer-Token aus dem Authorization-Header.
   * Erwartetes Format: "Authorization: Bearer <token>"
   * Gibt null zurück, wenn Format fehlt/ungültig ist.
   */
  private String parseBearer(String header) {
    if (header == null) return null;
    String p = header.trim();
    if (p.toLowerCase().startsWith("bearer ")) return p.substring(7).trim();
    return null;
  }
}
