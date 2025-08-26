package com.example.momolearn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.momolearn.security.AuthInterceptor;

/**
 * Zentrale Web-Konfiguration für Spring MVC.
 * 
 * Diese Klasse:
 *  - aktiviert Konfigurationen über @Configuration
 *  - implementiert WebMvcConfigurer, um CORS-Regeln und Interceptor-Registrierungen
 *    an einer Stelle zu bündeln.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  // Unser benutzerdefinierter Interceptor, der z. B. Authentifizierung/JWT prüft.
  private final AuthInterceptor auth;

  // Spring injiziert den AuthInterceptor (Constructor Injection ist bevorzugt, da immutable/testbar).
  public WebConfig(AuthInterceptor auth) {
    this.auth = auth;
  }

  /**
   * CORS (Cross-Origin Resource Sharing) erlaubt einem Frontend unter einer anderen Origin
   * (z. B. http://localhost:5173) Browseranfragen an dieses Backend zu schicken.
   * Ohne korrekte CORS-Header blockiert der Browser solche Requests.
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")                           // CORS gilt für alle Endpunkte des Backends
        .allowedOrigins("http://localhost:5173")         // nur diese Origin darf zugreifen (Vite/React-Dev-Server)
        .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS") // erlaubte HTTP-Methoden
        .allowedHeaders("*")                             // alle Request-Header erlaubt (z. B. Authorization, Content-Type)
        .exposedHeaders("Content-Disposition")           // dieser Response-Header ist für JS sichtbar (wichtig für Downloads/Dateinamen)
        .allowCredentials(true)                          // Cookies/Authorization-Header dürfen mitgesendet werden
        .maxAge(3600);                                   // Preflight-Cache-Dauer in Sekunden (reduziert Anzahl OPTIONS-Requests)
  }

  /**
   * Registrierung von HandlerInterceptors.
   * Ein Interceptor kann vor/ nach Controller-Aufrufen laufen (z. B. Auth-Check, Logging).
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(auth)                        // unseren AuthInterceptor registrieren
        .addPathPatterns("/users/**")                    // Interceptor greift auf allen /users/...-Routen (Schutzbereich)
        .excludePathPatterns(
            "/auth/**",                                  // Auth-Endpunkte bleiben frei (Login/Registrierung etc.)
            "/hello",                                    // simples Debug/Health-Endpoint bleibt frei
            "/debug/**"                                  // Debug-Endpunkte bleiben frei
        );
        // Ergebnis: Alle /users/**-Routen werden abgefangen,
        // außer wenn sie explizit über excludePathPatterns ausgenommen sind.
  }
}
