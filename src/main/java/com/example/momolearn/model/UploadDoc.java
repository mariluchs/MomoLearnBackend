package com.example.momolearn.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("uploads")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UploadDoc {
  @Id
  private String id;

  @Indexed
  private String userId;       // Besitzer

  private String filename;
  private String contentType;
  private long size;

  /** GridFS _id als String */
  private String storageId;

  private Instant uploadedAt;
}
