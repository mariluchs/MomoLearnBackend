package com.example.momolearn.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * MongoDB-Dokument, das eine einzelne Frage in einem StudySet repräsentiert.
 *
 * Gespeichert in der Collection "questions".
 */
@Document("questions")
@Getter
@Setter
@NoArgsConstructor       // Parameterloser Standard-Konstruktor
@AllArgsConstructor      // Konstruktor mit allen Feldern
@Builder                 // Builder-Pattern für bequemen Objektaufbau
public class Question {

  /** Eindeutige ID der Frage (von MongoDB generiert). */
  @Id
  private String id;

  /** Referenz auf das StudySet, zu dem diese Frage gehört. */
  private String studySetId;

  /** 
   * Der eigentliche Fragentext (z. B. "Was ist die Hauptstadt von Frankreich?").
   */
  private String stem;

  /** 
   * Liste der möglichen Antwortoptionen.
   * Erwartet werden genau 4 Strings.
   */
  private List<String> choices;

  /** 
   * Index der richtigen Antwort innerhalb der `choices`-Liste.
   * Gültige Werte: 0, 1, 2, 3
   */
  private int correctIndex;

  /** 
   * Optionale Erklärung oder Begründung für die richtige Antwort.
   * Kann im Frontend angezeigt werden, um dem Nutzer Feedback zu geben.
   */
  private String explanation;
}
