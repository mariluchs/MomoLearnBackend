package com.example.momolearn.repository;

import com.example.momolearn.model.AnswerAttempt;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository-Interface für den Zugriff auf die MongoDB-Collection "answer_attempts".
 *
 * Ermöglicht CRUD-Operationen und spezielle Abfragen für Antwort-Versuche.
 */
public interface AnswerAttemptRepository extends MongoRepository<AnswerAttempt, String> {

  /**
   * Holt alle Versuche eines bestimmten Benutzers, absteigend nach Erstellungszeit sortiert.
   *
   * @param userId ID des Benutzers
   * @return Liste aller Antwort-Versuche, neueste zuerst
   */
  List<AnswerAttempt> findByUserIdOrderByCreatedAtDesc(String userId);

  /**
   * Zählt, wie viele korrekte Antworten ein Benutzer insgesamt gegeben hat.
   *
   * @param userId ID des Benutzers
   * @return Anzahl der richtigen Antworten
   */
  long countByUserIdAndCorrectIsTrue(String userId);
}
