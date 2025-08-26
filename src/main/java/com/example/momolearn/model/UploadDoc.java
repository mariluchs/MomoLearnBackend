package com.example.momolearn.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB-Dokument, das Informationen zu einem hochgeladenen Dokument speichert.
 *
 * Diese Dateien können später von der KI genutzt werden,
 * um automatisch Fragen zu generieren (z. B. aus PDF-Skripten).
 */
@Document("uploads")           // Collection-Name in MongoDB
@Getter
@Setter
@NoArgsConstructor             // Standard-Konstruktor
@AllArgsConstructor            // Konstruktor mit allen Feldern
@Builder                       // Builder-Pattern für komfortable Objekt-Erstellung
public class UploadDoc {

  /** Eindeutige ID des Upload-Dokuments (MongoDB generiert). */
  @Id
  private String id;

  /** ID des Benutzers, dem dieses Dokument gehört. */
  @Indexed
  private String userId;

  /** Original-Dateiname der hochgeladenen Datei. */
  private String filename;

  /** MIME-Type des Dokuments, z. B. "application/pdf". */
  private String contentType;

  /** Größe der Datei in Bytes. */
  private long size;

  /**
   * ID des gespeicherten Inhalts in GridFS (MongoDB-Dateispeicher).
   * Dient zur Verknüpfung der Metadaten mit der eigentlichen Datei.
   */
  private String storageId;

  /** Zeitpunkt des Uploads. */
  private Instant uploadedAt;
}
