package com.example.momolearn.security;

import java.time.Instant;
import java.util.Optional;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.momolearn.model.SessionToken;
import com.example.momolearn.repository.SessionTokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor für Authentifizierung und Ownership-Check.
 *
 * Dieser wird in der WebConfig registriert und überprüft:
 * 1. Ob ein gültiger Bearer-Token vorhanden ist.
 * 2. Ob der Token nicht abgelaufen ist.
 * 3. Ob der Nutzer nur auf seine eigenen Ressourcen zugreift
 *    (Ownership-Check für /users/{userId}/...-Routen).
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

  private final SessionTokenRepository tokens;

  public AuthInterceptor(SessionTokenRepository tokens) {
    this.tokens = tokens;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    // ✅ 1) Preflight-Requests (OPTIONS) immer ohne Prüfung durchlassen
    if (HttpMethod.OPTIONS.matches(request.getMethod())) {
      return true;
    }

    // ✅ 2) Authentifizierung prüfen
    String auth = request.getHeader("Authorization");
    String token = parseBearer(auth);

    // Wenn kein Bearer-Token im Header, sofort 401 (Unauthorized)
    if (token == null) {
      response.sendError(HttpStatus.UNAUTHORIZED.value(), "Authorization Bearer Token fehlt");
      return false;
    }

    // Token in der DB suchen
    Optional<SessionToken> opt = tokens.findByToken(token);
    if (opt.isEmpty() || isExpired(opt.get())) {
      response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token ungültig/abgelaufen");
      return false;
    }

    // ✅ 3) Ownership-Check: 
    // Prüft, ob in der URL ein userId-Segment ist (z. B. /users/{userId}/...)
    // und ob diese ID mit der ID aus dem Token übereinstimmt.
    String pathUserId = extractUserIdFromPath(request.getRequestURI());
    if (pathUserId != null && !pathUserId.equals(opt.get().getUserId())) {
      response.sendError(HttpStatus.FORBIDDEN.value(), "Zugriff auf fremde Ressourcen verboten");
      return false;
    }

    // ✅ Wenn alles OK → Request weiterleiten
    return true;
  }

  /**
   * Prüft, ob der Token abgelaufen ist.
   */
  private boolean isExpired(SessionToken st) {
    return st.getExpiresAt() != null && st.getExpiresAt().isBefore(Instant.now());
  }

  /**
   * Extrahiert den Token-String aus dem "Authorization"-Header.
   * Erwartetes Format: "Bearer <token>"
   */
  private String parseBearer(String header) {
    if (header == null) return null;
    String p = header.trim();
    if (p.toLowerCase().startsWith("bearer ")) return p.substring(7).trim();
    return null;
  }

  /**
   * Liest aus der URL den userId-Teil, wenn sie wie /users/{userId}/... aufgebaut ist.
   * Beispiel:
   *   /api/users/123/courses -> gibt "123" zurück
   */
  private String extractUserIdFromPath(String uri) {
    if (uri == null) return null;
    String[] parts = uri.split("/");
    for (int i = 0; i < parts.length; i++) {
      if ("users".equals(parts[i]) && i + 1 < parts.length) {
        return parts[i + 1];
      }
    }
    return null;
  }
}
