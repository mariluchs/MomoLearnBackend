package com.example.momolearn.service;

import com.example.momolearn.dto.AttemptResultDto;
import com.example.momolearn.dto.UserStatsDto;
import com.example.momolearn.model.AnswerAttempt;
import com.example.momolearn.model.Question;
import com.example.momolearn.model.User;
import com.example.momolearn.repository.AnswerAttemptRepository;
import com.example.momolearn.repository.QuestionRepository;
import com.example.momolearn.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

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

    // --- XP/Level/Streak Regeln ---
    int base = correct ? 10 : 0;
    int bonus = correct ? Math.min(u.getStreak(), 5) : 0; // kleiner Bonus je Streak, max 5
    int xpAwarded = base + bonus;

    // Streak aktualisieren
    if (correct) {
      u.setStreak(u.getStreak() + 1);
    } else {
      u.setStreak(0);
    }
    u.setLastAnswerAt(Instant.now());

    // XP & Level
    int newXp = u.getXp() + xpAwarded;
    int newLevel = u.getLevel();
    while (newXp >= xpNeededForNext(newLevel)) {
      newXp -= xpNeededForNext(newLevel);
      newLevel++;
    }
    u.setXp(newXp);
    u.setLevel(newLevel);

    users.save(u);

    // Attempt speichern
    attempts.save(AnswerAttempt.builder()
        .userId(userId)
        .questionId(questionId)
        .chosenIndex(chosenIndex)
        .correct(correct)
        .scoredXp(xpAwarded)
        .createdAt(Instant.now())
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
