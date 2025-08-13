package com.example.momolearn.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

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

  // --- Gamification ---
  @Builder.Default
  private int xp = 0;

  @Builder.Default
  private int level = 1;

  @Builder.Default
  private int streak = 0;

  private Instant lastAnswerAt;
}
