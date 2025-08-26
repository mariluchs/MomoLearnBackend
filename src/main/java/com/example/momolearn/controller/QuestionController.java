package com.example.momolearn.controller;

import com.example.momolearn.model.Question;
import com.example.momolearn.model.StudySet;
import com.example.momolearn.repository.QuestionRepository;
import com.example.momolearn.service.StudySetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST-Controller für den Zugriff auf Fragen eines bestimmten Lern-Sets (StudySet).
 *
 * Basisroute: /users/{userId}/sets/{setId}/questions
 * Das Prefix /api kommt global aus der application.properties.
 */
@RestController
@RequestMapping("/users/{userId}/sets/{setId}/questions")
public class QuestionController {

  // Repository für direkten Datenbankzugriff auf Fragen
  private final QuestionRepository repo;

  // Service für Logik rund um StudySets (inklusive Ownership-Check)
  private final StudySetService sets;

  // Konstruktor-Injektion für Abhängigkeiten
  public QuestionController(QuestionRepository repo, StudySetService sets) {
    this.repo = repo;
    this.sets = sets;
  }

  /**
   * Listet alle Fragen eines bestimmten Lern-Sets.
   *
   * GET /users/{userId}/sets/{setId}/questions
   *
   * Ablauf:
   * 1. Prüfen, ob das angefragte Set existiert.
   * 2. Prüfen, ob das Set dem angegebenen Nutzer gehört (Ownership-Check).
   * 3. Alle Fragen, die zu diesem Set gehören, aus der Datenbank holen und zurückgeben.
   *
   * @param userId ID des Nutzers (aus der URL)
   * @param setId  ID des Sets (aus der URL)
   * @return Liste aller Fragen, die zu diesem Set gehören
   *
   * @throws ResponseStatusException 404, wenn das Set nicht existiert
   * @throws ResponseStatusException 403, wenn der Nutzer nicht Eigentümer des Sets ist
   */
  @GetMapping
  public List<Question> list(@PathVariable String userId, @PathVariable String setId) {
    // Ownership prüfen: Existenz des Sets checken
    StudySet s = sets.get(setId);
    if (s == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lern-Set nicht gefunden");
    }

    // Prüfen, ob dieses Set auch wirklich zu dem angegebenen Nutzer gehört
    if (!userId.equals(s.getUserId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Set gehört nicht zum Nutzer");
    }

    // Fragen, die zu diesem Lern-Set gehören, aus der DB laden und zurückgeben
    return repo.findAllByStudySetId(setId);
  }
}
