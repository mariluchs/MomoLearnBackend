package com.example.momolearn.controller;

import com.example.momolearn.model.User;
import com.example.momolearn.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * REST-Controller für grundlegende CRUD-Operationen auf Benutzern.
 *
 * Basis-Route: /users
 */
@RestController
@RequestMapping("/users")
public class UserController {

  // Repository für Datenbankoperationen auf User-Entities
  private final UserRepository users;

  public UserController(UserRepository users) {
    this.users = users;
  }

  /**
   * Erstellt einen neuen Benutzer.
   *
   * POST /users
   *
   * Ablauf:
   * 1. Setzt die ID auf null (damit die DB eine neue generiert)
   * 2. Setzt das aktuelle Erstellungsdatum
   * 3. Speichert den Benutzer in der Datenbank
   *
   * @param u Benutzerobjekt aus dem Request-Body
   * @return der gespeicherte Benutzer (inkl. generierter ID)
   *
   * Rückgabe: HTTP 201 Created
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public User create(@RequestBody User u) {
    u.setId(null);                   // ID zurücksetzen, falls im Request gesetzt
    u.setCreatedAt(Instant.now());   // Erstellungszeit setzen
    return users.save(u);            // in DB speichern
  }

  /**
   * Liefert alle Benutzer zurück.
   *
   * GET /users
   *
   * @return Liste aller Benutzer in der Datenbank
   */
  @GetMapping
  public List<User> list() {
    return users.findAll();
  }

  /**
   * Holt einen bestimmten Benutzer anhand seiner ID.
   *
   * GET /users/{userId}
   *
   * @param userId ID des Benutzers
   * @return Benutzerobjekt
   * @throws org.springframework.web.server.ResponseStatusException 404, wenn Benutzer nicht existiert
   */
  @GetMapping("/{userId}")
  public User get(@PathVariable String userId) {
    return users.findById(userId)
        .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));
  }

  /**
   * Aktualisiert bestimmte Felder eines Benutzers.
   *
   * PUT /users/{userId}
   *
   * Ablauf:
   * 1. Holt den aktuellen Benutzer aus der DB.
   * 2. Aktualisiert Felder, die im Patch-Request übergeben wurden.
   * 3. Speichert die aktualisierte Version des Benutzers.
   *
   * @param userId ID des Benutzers
   * @param patch  JSON-Objekt mit zu aktualisierenden Feldern (Name, Email)
   * @return der aktualisierte Benutzer
   */
  @PutMapping("/{userId}")
  public User update(@PathVariable String userId, @RequestBody User patch) {
    User cur = get(userId);                    // existierenden Benutzer laden
    if (patch.getName() != null) cur.setName(patch.getName());
    if (patch.getEmail() != null) cur.setEmail(patch.getEmail());
    return users.save(cur);                    // Änderungen speichern
  }

  /**
   * Löscht einen Benutzer anhand der ID.
   *
   * DELETE /users/{userId}
   *
   * Rückgabe: HTTP 204 No Content, wenn erfolgreich gelöscht.
   */
  @DeleteMapping("/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String userId) {
    users.deleteById(userId);
  }
}
