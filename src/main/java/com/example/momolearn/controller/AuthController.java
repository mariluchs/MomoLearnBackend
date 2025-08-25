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

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final UserRepository users;
  private final SessionTokenRepository tokens;

  public AuthController(UserRepository users, SessionTokenRepository tokens) {
    this.users = users;
    this.tokens = tokens;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
    users.findByEmail(req.getEmail()).ifPresent(u -> {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "E-Mail bereits vergeben");
    });

    String hash = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt());

    User u = users.save(User.builder()
        .name(req.getName().trim())
        .email(req.getEmail().trim().toLowerCase())
        .passwordHash(hash)
        .createdAt(Instant.now())
        .xp(0).level(1).streak(0)
        .build());

    String token = issueToken(u.getId());

    return AuthResponse.builder()
        .userId(u.getId())
        .name(u.getName())
        .email(u.getEmail())
        .token(token)
        .build();
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest req) {
    User u = users.findByEmail(req.getEmail().trim().toLowerCase())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ungültige Anmeldedaten"));
    if (u.getPasswordHash() == null || !BCrypt.checkpw(req.getPassword(), u.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ungültige Anmeldedaten");
    }
    String token = issueToken(u.getId());
    return AuthResponse.builder()
        .userId(u.getId())
        .name(u.getName())
        .email(u.getEmail())
        .token(token)
        .build();
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(@RequestHeader(value = "Authorization", required = false) String auth) {
    String token = parseBearer(auth);
    if (token != null) tokens.deleteByToken(token);
  }

  @GetMapping("/me")
  public PublicUserDto me(@RequestHeader(value = "Authorization", required = false) String auth) {
    String token = parseBearer(auth);
    var st = tokens.findByToken(token)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Kein gültiger Token"));
    return users.findById(st.getUserId())
        .map(u -> PublicUserDto.builder().id(u.getId()).name(u.getName()).email(u.getEmail()).build())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Benutzer unbekannt"));
  }

  // --- Helpers ---

  private String issueToken(String userId) {
    String tok = UUID.randomUUID().toString();
    tokens.save(SessionToken.builder()
        .token(tok)
        .userId(userId)
        .createdAt(Instant.now())
        .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
        .build());
    return tok;
  }

  private String parseBearer(String header) {
    if (header == null) return null;
    String p = header.trim();
    if (p.toLowerCase().startsWith("bearer ")) return p.substring(7).trim();
    return null;
  }
}
