package com.example.momolearn.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB-Dokument, das ein "StudySet" (Lern-Set) repräsentiert.
 *
 * Ein StudySet ist ein Sammlung von Fragen, die z. B.:
 *  - aus einem hochgeladenen Dokument (uploadId) generiert werden
 *  - einem Kurs zugeordnet sind
 *  - einem bestimmten Nutzer gehören
 */
@Document("study_sets")          // MongoDB-Collection-Name
@Getter
@Setter
@NoArgsConstructor               // Standard-Konstruktor
@AllArgsConstructor              // Konstruktor mit allen Feldern
@Builder                         // Builder-Pattern für bequeme Objekterstellung
public class StudySet {

  /** Eindeutige ID des StudySets, von MongoDB generiert. */
  @Id
  private String id;

  /** ID des Nutzers, dem dieses Set gehört. */
  @Indexed
  private String userId;

  /** ID des Kurses, zu dem dieses Set gehört. */
  private String courseId;

  /** Titel des Lern-Sets (z. B. "Kapitel 1: Grundlagen"). */
  private String title;

  /** 
   * ID des hochgeladenen Dokuments (UploadDoc), 
   * das für die Generierung dieses Sets verwendet wird.
   */
  private String uploadId;

  /**
   * Status des Sets:
   *  - PENDING: Die KI-Generierung läuft noch.
   *  - READY: Die Fragen sind fertig generiert.
   *  - FAILED: Die Generierung ist fehlgeschlagen.
   */
  private Status status;

  /** Zeitstempel der Erstellung des Sets. */
  private Instant createdAt;

  /** Status-Enum für das Feld status. */
  public enum Status {
    PENDING, // Generierung läuft noch
    READY,   // Fragen wurden erfolgreich generiert
    FAILED   // Generierung fehlgeschlagen
  }
}
