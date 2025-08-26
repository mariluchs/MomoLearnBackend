package com.example.momolearn.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.momolearn.model.Question;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class AiQuestionGenerator {

  private static final Logger log = LoggerFactory.getLogger(AiQuestionGenerator.class);

  // HTTP-Client für Anfragen an die DeepSeek-API
  private final WebClient http;
  private final String model;
  private final ObjectMapper mapper = new ObjectMapper();
  private final int timeoutMs;
  private final int clipChars;
  private final int maxTokens;

  /**
   * Konstruktor zum Initialisieren des HTTP-Clients für DeepSeek.
   *
   * Konfiguration wird über application.properties oder Umgebungsvariablen geladen:
   * - deepseek.api.key
   * - deepseek.api.base-url
   * - deepseek.api.model
   * - deepseek.timeout-ms
   * - deepseek.prompt.clip-chars
   * - deepseek.max-tokens
   */
  public AiQuestionGenerator(
      @Value("${deepseek.api.base-url:https://api.deepseek.com}") String baseUrl,
      @Value("${deepseek.api.key:${DEEPSEEK_API_KEY:}}") String apiKey,
      @Value("${deepseek.api.model:deepseek-chat}") String model,
      @Value("${deepseek.timeout-ms:90000}") int timeoutMs,
      @Value("${deepseek.prompt.clip-chars:8000}") int clipChars,
      @Value("${deepseek.max-tokens:2500}") int maxTokens
  ) {
    if (apiKey == null || apiKey.isBlank()) {
      throw new IllegalStateException("DeepSeek API-Key fehlt. Setze ENV DEEPSEEK_API_KEY oder Property deepseek.api.key.");
    }

    // Basis-URL säubern, um doppelte Slashes zu vermeiden
    String cleanBase = trimTrailingSlash(baseUrl);
    String auth = "Bearer " + apiKey.trim();

    this.model = model;
    this.timeoutMs = timeoutMs;
    this.clipChars = Math.max(1000, clipChars); // Mindestwert für abgeschnittenen Text
    this.maxTokens = Math.max(256, maxTokens);  // Mindestwert für Token-Limit

    // WebClient für API-Aufrufe vorbereiten
    this.http = WebClient.builder()
        .baseUrl(cleanBase)
        .defaultHeader(HttpHeaders.AUTHORIZATION, auth)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build();

    log.info("DeepSeek client ready. base='{}', model='{}', timeoutMs={}, clipChars={}, maxTokens={}",
        cleanBase, this.model, this.timeoutMs, this.clipChars, this.maxTokens);
  }

  /**
   * Generiert automatisch so viele Fragen, wie sinnvoll sind.
   * Die KI entscheidet selbst, wie viele Fragen sie aus dem gegebenen Text extrahiert.
   *
   * @param studySetId ID des StudySets, dem die Fragen zugeordnet werden
   * @param sourceText Der Inhalt, aus dem Fragen generiert werden sollen (z. B. Text aus einer PDF)
   * @return Liste von validierten Question-Objekten
   */
  public List<Question> generateDecideCount(String studySetId, String sourceText) throws Exception {
    // Text vorbereiten und ggf. auf konfigurierten Maximalwert kürzen
    String clipped = (sourceText == null ? "" : sourceText.trim());
    if (clipped.length() > clipChars) clipped = clipped.substring(0, clipChars);

    // System-Prompt: erklärt der KI, wie sie antworten soll
    String system = """
        Du bist ein Tutor. Erstelle zu dem gegebenen Lehrtext sinnvolle Multiple-Choice-Fragen.
        Anforderungen:
        - Antworte AUSSCHLIESSLICH als JSON-Objekt mit dem Feld "questions" (keine Erklärtexte außerhalb von JSON).
        - Jede Frage: { "stem": string, "choices": string[4], "correctIndex": 0-3, "explanation": string? }.
        - Erstelle so viele Fragen, wie fachlich sinnvoll sind keine Duplikate.
        - Nutze klare, prägnante Antworten; genau eine richtige Lösung.
        """;

    // User-Prompt: enthält den tatsächlichen Lerntext
    String user = """
        Lehrtext:
        ---
        %s
        ---
        Bitte liefere NUR folgendes JSON:
        { "questions": [ { "stem": "...", "choices": ["...","...","...","..."], "correctIndex": 0, "explanation": "..." }, ... ] }
        """.formatted(clipped);

    // JSON-Request für DeepSeek bauen
    var body = mapper.createObjectNode();
    body.put("model", model);
    var msgs = mapper.createArrayNode();
    msgs.add(mapper.createObjectNode().put("role", "system").put("content", system));
    msgs.add(mapper.createObjectNode().put("role", "user").put("content", user));
    body.set("messages", msgs);
    body.set("response_format", mapper.createObjectNode().put("type", "json_object"));
    body.put("temperature", 0.3);
    body.put("max_tokens", maxTokens);

    log.debug("DeepSeek request: chars={}, max_tokens={}", clipped.length(), maxTokens);

    // API-Aufruf an DeepSeek
    String raw = http.post()
        .uri("chat/completions")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .exchangeToMono(res -> res.bodyToMono(String.class).flatMap(b -> {
          if (res.statusCode().is2xxSuccessful()) {
            return Mono.just(b);
          } else {
            // Fehlertext kürzen, um Logs übersichtlich zu halten
            String snip = b == null ? "" : b.substring(0, Math.min(600, b.length()));
            String msg = "DeepSeek HTTP " + res.statusCode().value() + " – " + res.statusCode()
                + (snip.isBlank() ? "" : (" | body: " + snip));
            return Mono.error(new IllegalStateException(msg));
          }
        }))
        .timeout(Duration.ofMillis(timeoutMs))
        .retryWhen(
            Retry.backoff(2, Duration.ofMillis(500))
                .doBeforeRetry(sig -> log.warn("DeepSeek retry #{} cause={}", sig.totalRetries()+1, sig.failure().toString()))
                .filter(err -> {
                  // Nur bei Timeout, Netzwerkfehlern oder Serverfehlern erneut versuchen
                  if (err instanceof TimeoutException) return true;
                  if (err instanceof WebClientRequestException) return true;
                  if (err instanceof WebClientResponseException wre) {
                    int sc = wre.getStatusCode().value();
                    return sc == 429 || (sc >= 500 && sc < 600);
                  }
                  return false;
                })
        )
        .doOnError(e -> log.error("DeepSeek call failed: {}", e.toString()))
        .block();

    if (raw == null || raw.isBlank()) {
      throw new IllegalStateException("Leere Antwort von DeepSeek erhalten.");
    }

    // Antwort parsen
    JsonNode root = mapper.readTree(raw);
    String content = root.path("choices").path(0).path("message").path("content").asText();
    content = stripCodeFences(content); // Entfernt ```json```-Fences, falls vorhanden

    JsonNode json = mapper.readTree(content);
    JsonNode arr = json.path("questions");
    if (!arr.isArray() || arr.size() == 0) {
      throw new IllegalStateException("KI-Antwort enthielt keine questions[].");
    }

    // Fragen extrahieren und validieren
    List<Question> out = new ArrayList<>();
    for (JsonNode q : arr) {
      String stem = q.path("stem").asText("");
      JsonNode choicesNode = q.path("choices");
      int correctIndex = q.path("correctIndex").asInt(-1);
      String explanation = q.path("explanation").asText(null);

      // Nur gültige Fragen übernehmen
      if (stem.isBlank() || !choicesNode.isArray() || choicesNode.size() != 4 || correctIndex < 0 || correctIndex > 3) {
        continue;
      }

      List<String> choices = new ArrayList<>(4);
      for (int i = 0; i < 4; i++) choices.add(choicesNode.get(i).asText(""));

      out.add(Question.builder()
          .studySetId(studySetId)
          .stem(stem)
          .choices(choices)
          .correctIndex(correctIndex)
          .explanation(explanation == null || explanation.isBlank() ? null : explanation)
          .build());
    }

    if (out.isEmpty()) {
      throw new IllegalStateException("Keine gültigen Fragen in der KI-Antwort.");
    }

    log.debug("DeepSeek parsed {} questions", out.size());
    return out;
  }

  /** Entfernt ```json```-Fences aus der KI-Antwort, falls vorhanden. */
  private static String stripCodeFences(String s) {
    if (s == null) return "";
    String t = s.trim();
    if (t.startsWith("```")) {
      int first = t.indexOf('\n');
      int last = t.lastIndexOf("```");
      if (first != -1 && last != -1 && last > first) {
        return t.substring(first + 1, last).trim();
      }
    }
    return t;
  }

  /** Entfernt einen eventuellen Slash am Ende einer URL. */
  private static String trimTrailingSlash(String s) {
    if (s == null) return "";
    return s.endsWith("/") ? s.substring(0, s.length()-1) : s;
  }
}
