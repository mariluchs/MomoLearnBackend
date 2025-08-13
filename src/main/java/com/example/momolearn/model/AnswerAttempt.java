package com.example.momolearn.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("answer_attempts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AnswerAttempt {
  @Id
  private String id;

  private String userId;
  private String questionId;

  private int chosenIndex;
  private boolean correct;

  private int scoredXp;

  private Instant createdAt;
}
