package com.example.momolearn.controller;

import com.example.momolearn.dto.AttemptRequest;
import com.example.momolearn.dto.AttemptResultDto;
import com.example.momolearn.service.GamificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// REST-Controller für "Versuche" (attempts) bei Fragen
@RestController
@RequestMapping("/users/{userId}/questions/{questionId}/attempts")
public class AttemptController {

  // Service, der die Geschäftslogik für Gamification/Attempts bereitstellt
  private final GamificationService service;

  // Konstruktor-Injection: Spring liefert automatisch die Instanz von GamificationService
  public AttemptController(GamificationService service) {
    this.service = service;
  }

  /**
   * POST-Endpunkt zum Absenden eines neuen Antwort-Versuchs.
  @param userId ID des Benutzers (aus der URL)
  @param questionId ID der Frage (aus der URL)
  @param req enthält den ausgewählten Antwort-Index (kommt aus dem Request-Body)
  @return AttemptResultDto mit dem Ergebnis des Versuchs (z. B. richtig/falsch, Punkte)
   */
  
   @PostMapping
  @ResponseStatus(HttpStatus.CREATED) // Gibt HTTP 201 zurück, wenn erfolgreich erstellt
  public AttemptResultDto attempt(@PathVariable String userId,
                                  @PathVariable String questionId,
                                  @Valid @RequestBody AttemptRequest req) {
    // Delegiert an den Service: verarbeitet den Versuch und gibt Ergebnis zurück
    return service.recordAttempt(userId, questionId, req.getChosenIndex());
  }
}
