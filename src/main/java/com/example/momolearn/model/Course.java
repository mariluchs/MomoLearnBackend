package com.example.momolearn.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB-Dokument für einen Kurs.
 *
 * Wird in der Collection "courses" gespeichert.
 * Jeder Kurs gehört einem bestimmten Benutzer (userId).
 */
@Document("courses")
@Getter
@Setter
@NoArgsConstructor       // Erstellt einen leeren Standard-Konstruktor
@AllArgsConstructor      // Erstellt einen Konstruktor mit allen Feldern
@Builder                 // Erlaubt das Erstellen von Objekten über das Builder-Pattern
public class Course {

  /** Eindeutige ID des Kurses (wird von MongoDB generiert). */
  @Id
  private String id;

  /** ID des Besitzers des Kurses; indexiert für schnelle Abfragen. */
  @Indexed
  private String userId;

  /** Titel des Kurses (Pflichtfeld). */
  @NotBlank
  private String title;

  /** Optionale Beschreibung des Kurses. */
  private String description;
}
