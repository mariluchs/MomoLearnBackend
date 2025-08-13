package com.example.momolearn.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("study_sets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudySet {
  @Id
  private String id;

  @Indexed
  private String userId;     // Besitzer

  private String courseId;
  private String title;
  private String uploadId;   // UploadDoc.id
  private Status status;
  private Instant createdAt;

  public enum Status { PENDING, READY, FAILED }
}
