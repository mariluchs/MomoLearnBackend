package com.example.momolearn.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("questions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Question {
  @Id
  private String id;

  private String studySetId;

  private String stem;           // Fragentext
  private List<String> choices;  // genau 4
  private int correctIndex;      // 0..3
  private String explanation;    // optional
}
