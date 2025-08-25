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

@Component
public class AuthInterceptor implements HandlerInterceptor {

  private final SessionTokenRepository tokens;

  public AuthInterceptor(SessionTokenRepository tokens) {
    this.tokens = tokens;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    // ✅ 1) Preflight (OPTIONS) immer durchlassen – kein Auth-Check
    if (HttpMethod.OPTIONS.matches(request.getMethod())) {
      return true;
    }

    // ✅ 2) Nur echte Requests prüfen (GET/POST/PUT/PATCH/DELETE)
    String auth = request.getHeader("Authorization");
    String token = parseBearer(auth);
    if (token == null) {
      response.sendError(HttpStatus.UNAUTHORIZED.value(), "Authorization Bearer Token fehlt");
      return false;
    }

    Optional<SessionToken> opt = tokens.findByToken(token);
    if (opt.isEmpty() || isExpired(opt.get())) {
      response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token ungültig/abgelaufen");
      return false;
    }

    // ✅ 3) Pfad-basierter Ownership-Check (nur wenn /users/{userId}/... vorhanden)
    String pathUserId = extractUserIdFromPath(request.getRequestURI());
    if (pathUserId != null && !pathUserId.equals(opt.get().getUserId())) {
      response.sendError(HttpStatus.FORBIDDEN.value(), "Zugriff auf fremde Ressourcen verboten");
      return false;
    }

    return true;
  }

  private boolean isExpired(SessionToken st) {
    return st.getExpiresAt() != null && st.getExpiresAt().isBefore(Instant.now());
  }

  private String parseBearer(String header) {
    if (header == null) return null;
    String p = header.trim();
    if (p.toLowerCase().startsWith("bearer ")) return p.substring(7).trim();
    return null;
  }

  private String extractUserIdFromPath(String uri) {
    // Beispiel: /api/users/{userId}/...  oder /users/{userId}/...
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
 