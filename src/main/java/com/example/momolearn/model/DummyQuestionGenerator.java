package com.example.momolearn.model;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.example.momolearn.service.QuestionGenerator;

import java.text.BreakIterator;
import java.util.*;

@Service
@ConditionalOnProperty(name = "ai.enabled", havingValue = "false", matchIfMissing = true)
public class DummyQuestionGenerator implements QuestionGenerator {

  private static final int MIN_LEN = 50;
  private static final int MAX_LEN = 220;
  private final Random rnd = new Random();

  @Override
  public List<Question> generate(String studySetId, String text, int count) {
    List<String> sentences = splitGerman(text);
    List<String> candidates = sentences.stream()
        .map(String::trim)
        .filter(this::looksGood)
        .distinct()
        .toList();

    int n = Math.min(count, candidates.size());
    List<Question> out = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      String correct = candidates.get(i);
      if (correct.length() > MAX_LEN) correct = correct.substring(0, MAX_LEN - 3) + "...";
      List<String> choices = new ArrayList<>(List.of(
          correct,
          "Diese Aussage steht nicht im Skript.",
          "Keine der anderen Antworten ist korrekt.",
          "Die Aussage ist frei erfunden."
      ));
      Collections.shuffle(choices, rnd);
      out.add(Question.builder()
          .studySetId(studySetId)
          .stem("Welche Aussage stammt aus dem Skript?")
          .choices(choices)
          .correctIndex(choices.indexOf(correct))
          .explanation("Originalsatz aus dem hochgeladenen Skript.")
          .build());
    }
    return out;
  }

  private List<String> splitGerman(String text) {
    BreakIterator bi = BreakIterator.getSentenceInstance(Locale.GERMAN);
    bi.setText(text);
    List<String> parts = new ArrayList<>();
    for (int start = bi.first(), end = bi.next(); end != BreakIterator.DONE; start = end, end = bi.next()) {
      String s = text.substring(start, end).trim();
      if (!s.isEmpty()) parts.add(s);
    }
    return parts;
  }

  private boolean looksGood(String s) {
    if (s.length() < MIN_LEN) return false;
    if (s.endsWith(":")) return false;
    if (s.split("\\s+").length < 6) return false;
    if (s.contains("©") || s.toLowerCase().contains("abbildung")) return false;
    return true;
    }
}
