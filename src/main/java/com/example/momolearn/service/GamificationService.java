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

  public AttemptResultDto recordAttempt(String userId, String questionId, int chosenIndex) {
    User u = users.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Question q = questions.findById(questionId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

    if (chosenIndex < 0 || chosenIndex > 3) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "chosenIndex must be 0..3");
    }

    boolean correct = (chosenIndex == q.getCorrectIndex());

    // === Tagesbasierte Streak-Logik (UTC) ===
    Instant now = Instant.now();
    LocalDate today = LocalDate.now(ZoneOffset.UTC);
    LocalDate lastDay = (u.getLastAnswerAt() == null)
        ? null
        : u.getLastAnswerAt().atOffset(ZoneOffset.UTC).toLocalDate();

    int newStreak = u.getStreak();
    if (lastDay == null) {
      // Erster Versuch überhaupt -> Streak startet bei 1
      newStreak = 1;
    } else if (lastDay.isEqual(today)) {
      // Heute schon gespielt -> Streak bleibt unverändert
      newStreak = u.getStreak();
    } else if (lastDay.plusDays(1).isEqual(today)) {
      // Genau gestern aktiv -> +1
      newStreak = u.getStreak() + 1;
    } else {
      // Mindestens einen Tag ausgelassen -> Reset und heute neu starten
      newStreak = 1;
    }

    // --- XP/Level/Streak Regeln ---
    int base = correct ? 10 : 0;
    int bonus = correct ? Math.min(newStreak, 5) : 0; // kleiner Bonus je Tages-Streak, max 5
    int xpAwarded = base + bonus;

    // XP & Level
    int accumulatedXp = u.getXp() + xpAwarded;
    int newLevel = u.getLevel();
    while (accumulatedXp >= xpNeededForNext(newLevel)) {
      accumulatedXp -= xpNeededForNext(newLevel);
      newLevel++;
    }

    // User speichern
    u.setStreak(newStreak);
    u.setLastAnswerAt(now);
    u.setXp(accumulatedXp);
    u.setLevel(newLevel);
    users.save(u);

    // Attempt speichern (Audit)
    attempts.save(AnswerAttempt.builder()
        .userId(userId)
        .questionId(questionId)
        .chosenIndex(chosenIndex)
        .correct(correct)
        .scoredXp(xpAwarded)
        .createdAt(now)
        .build());

    return AttemptResultDto.builder()
        .correct(correct)
        .xpAwarded(xpAwarded)
        .newUserXp(u.getXp())
        .newUserLevel(u.getLevel())
        .streak(u.getStreak())
        .build();
  }

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

  // einfache Levelkurve: 100 XP je Level
  private int xpNeededForNext(int level) {
    return 100;
  }
}
