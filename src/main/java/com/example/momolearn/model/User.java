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

@Document("users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
  @Id
  private String id;

  @NotBlank
  private String name;

  @Email
  @Indexed(unique = true)
  private String email;

  private Instant createdAt;

  /** Gespeicherter Passwort-Hash (BCrypt). Wird NICHT serialisiert. */
  @JsonIgnore
  private String passwordHash;

  // --- Gamification ---
  @Builder.Default
  private int xp = 0;

  @Builder.Default
  private int level = 1;

  @Builder.Default
  private int streak = 0;

  private Instant lastAnswerAt;
}
