package com.example.momolearn.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.momolearn.dto.AttemptResultDto;
import com.example.momolearn.dto.UserStatsDto;
import com.example.momolearn.model.AnswerAttempt;
import com.example.momolearn.model.Question;
import com.example.momolearn.model.User;
import com.example.momolearn.repository.AnswerAttemptRepository;
import com.example.momolearn.repository.QuestionRepository;
import com.example.momolearn.repository.UserRepository;

@Service
public class GamificationService {

  private final UserRepository users;
  private final QuestionRepository questions;
  private final AnswerAttemptRepository attempts;

  public GamificationService(UserRepository users,
                             QuestionRepository questions,
                             AnswerAttemptRepository attempts) {
    this.users = users;
    this.questions = questions;
    this.attempts = attempts;
  }

  /**
   * Verarbeitet einen Antwortversuch eines Nutzers:
   * - Prüft, ob Nutzer und Frage existieren
   * - Berechnet XP, Level und Streak
   * - Speichert den Versuch in der Datenbank
   * - Aktualisiert die Nutzerstatistiken
   *
   * @param userId       ID des Nutzers
   * @param questionId   ID der Frage
   * @param chosenIndex  Gewählter Antwortindex (0..3)
   * @return Ergebnis mit Informationen für das Frontend
   */
  public AttemptResultDto recordAttempt(String userId, String questionId, int chosenIndex) {
    // --- 1) Nutzer prüfen ---
    User u = users.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    // --- 2) Frage prüfen ---
    Question q = questions.findById(questionId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

    // --- 3) Validierung des Antwort-Index ---
    if (chosenIndex < 0 || chosenIndex > 3) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "chosenIndex must be 0..3");
    }

    // --- 4) Korrektheit prüfen ---
    boolean correct = (chosenIndex == q.getCorrectIndex());

    // --- 5) Streak-Logik (basierend auf UTC-Datum) ---
    Instant now = Instant.now();
    LocalDate today = LocalDate.now(ZoneOffset.UTC);
    LocalDate lastDay = (u.getLastAnswerAt() == null)
        ? null
        : u.getLastAnswerAt().atOffset(ZoneOffset.UTC).toLocalDate();

    int newStreak;
    if (lastDay == null) {
      // Erster Versuch → Streak startet bei 1
      newStreak = 1;
    } else if (lastDay.isEqual(today)) {
      // Schon heute aktiv → Streak bleibt gleich
      newStreak = u.getStreak();
    } else if (lastDay.plusDays(1).isEqual(today)) {
      // Gestern aktiv → Streak +1
      newStreak = u.getStreak() + 1;
    } else {
      // Pause → Streak-Reset
      newStreak = 1;
    }

    // --- 6) XP- und Level-Berechnung ---
    int base = correct ? 10 : 0;                    // Grund-XP für richtige Antwort
    int bonus = correct ? Math.min(newStreak, 5) : 0; // Bonus je Tages-Streak (max +5)
    int xpAwarded = base + bonus;

    int accumulatedXp = u.getXp() + xpAwarded;
    int newLevel = u.getLevel();

    // Levelaufstieg: alle 100 XP
    while (accumulatedXp >= xpNeededForNext(newLevel)) {
      accumulatedXp -= xpNeededForNext(newLevel);
      newLevel++;
    }

    // --- 7) Nutzer aktualisieren ---
    u.setStreak(newStreak);
    u.setLastAnswerAt(now);
    u.setXp(accumulatedXp);
    u.setLevel(newLevel);
    users.save(u);

    // --- 8) Versuch speichern ---
    attempts.save(AnswerAttempt.builder()
        .userId(userId)
        .questionId(questionId)
        .chosenIndex(chosenIndex)
        .correct(correct)
        .scoredXp(xpAwarded)
        .createdAt(now)
        .build());

    // --- 9) Ergebnis zurückgeben ---
    return AttemptResultDto.builder()
        .correct(correct)
        .xpAwarded(xpAwarded)
        .newUserXp(u.getXp())
        .newUserLevel(u.getLevel())
        .streak(u.getStreak())
        .build();
  }

  /**
   * Gibt die aktuellen Statistiken eines Nutzers zurück:
   * - XP, Level, Streak
   * - Anzahl der korrekt beantworteten Fragen
   *
   * @param userId ID des Nutzers
   * @return UserStatsDto mit den aktuellen Werten
   */
  public UserStatsDto stats(String userId) {
    User u = users.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    long correct = attempts.countByUserIdAndCorrectIsTrue(userId);

    return UserStatsDto.builder()
        .xp(u.getXp())
        .level(u.getLevel())
        .streak(u.getStreak())
        .correctAnswers(correct)
        .build();
  }

  /**
   * Gibt die benötigte XP-Menge für den nächsten Levelaufstieg zurück.
   * Aktuell ist die Levelkurve linear: 100 XP je Level.
   *
   * @param level Aktuelles Level
   * @return XP-Bedarf für das nächste Level
   */
  private int xpNeededForNext(int level) {
    return 100;
  }
}
