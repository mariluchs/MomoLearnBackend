package com.example.momolearn.repository;

import com.example.momolearn.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

/**
 * Repository-Interface für die MongoDB-Collection "users".
 *
 * Bietet Standard-CRUD-Operationen und eine zusätzliche Methode
 * zur Suche nach einem Benutzer anhand seiner E-Mail-Adresse.
 */
public interface UserRepository extends MongoRepository<User, String> {

  /**
   * Sucht einen Benutzer anhand seiner E-Mail-Adresse.
   *
   * @param email E-Mail des Benutzers
   * @return Optional<User> – leer, wenn kein Benutzer mit dieser E-Mail gefunden wurde
   *
   * Nützlich bei:
   * - Registrierung (Prüfen, ob E-Mail bereits existiert)
   * - Login (Benutzer anhand E-Mail laden)
   */
  Optional<User> findByEmail(String email);
}
