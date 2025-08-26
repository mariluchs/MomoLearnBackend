package com.example.momolearn.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.momolearn.model.SessionToken;

/**
 * Repository-Interface für die MongoDB-Collection "session_tokens".
 *
 * Ermöglicht Standard-CRUD-Operationen und spezielle Abfragen für Session-Tokens,
 * die bei Authentifizierung und Autorisierung genutzt werden.
 */
public interface SessionTokenRepository extends MongoRepository<SessionToken, String> {

  /**
   * Sucht einen Session-Token anhand des Token-Strings.
   *
   * @param token Der eindeutige Token-String (z. B. Bearer-Token)
   * @return Optional mit dem gefundenen SessionToken, falls vorhanden
   */
  Optional<SessionToken> findByToken(String token);

  /**
   * Löscht einen Session-Token anhand des Token-Strings.
   *
   * @param token Der eindeutige Token-String
   */
  void deleteByToken(String token);
}
