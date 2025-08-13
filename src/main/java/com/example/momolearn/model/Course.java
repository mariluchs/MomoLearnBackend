package com.example.momolearn.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("courses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Course {
  @Id
  private String id;

  @Indexed           // Besitzer des Kurses
  private String userId;

  @NotBlank
  private String title;

  private String description;
}
