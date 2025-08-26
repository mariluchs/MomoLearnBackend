// src/main/java/com/example/momolearn/controller/DebugController.java
package com.example.momolearn.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * Ein Debug-Controller, um Informationen zur KI-Integration (DeepSeek) abzufragen.
 * 
 * Der Endpunkt wird meist genutzt, um zu prüfen:
 *  - ob ein API-Key gesetzt ist
 *  - welche Basis-URL und welches Modell gerade konfiguriert sind
 */
@RestController
@RequestMapping("/api/debug")
public class DebugController {

  /**
   * Liefert Debug-Infos zur DeepSeek-Konfiguration.
   *
   * GET /api/debug/deepseek
   * 
   * Die @Value-Annotationen lesen Konfigurationswerte aus:
   *  1. aus application.properties (z.B. deepseek.api.key)
   *  2. aus Umgebungsvariablen (z.B. DEEPSEEK_API_KEY)
   *  3. oder verwenden Standardwerte (z.B. "https://api.deepseek.com")
   *
   * @return Ein JSON-Objekt mit den wichtigsten Konfigurationswerten:
   *   {
   *     "hasKey": true/false,  // ob ein API-Key vorhanden ist
   *     "base": "...",         // Basis-URL des DeepSeek-APIs
   *     "model": "..."         // Modellname, z.B. "deepseek-chat"
   *   }
   */
  @GetMapping("/deepseek")
  public Map<String,Object> deepseek(
      @Value("${deepseek.api.key:${DEEPSEEK_API_KEY:}}") String key,
      @Value("${deepseek.api.base-url:${deepseek.api.base:https://api.deepseek.com}}") String base,
      @Value("${deepseek.api.model:${deepseek.model:deepseek-chat}}") String model
  ) {
    // Gibt eine Map zurück, die als JSON an den Client serialisiert wird.
    return Map.of(
        "hasKey", key != null && !key.isBlank(), // zeigt an, ob ein Key gesetzt ist
        "base", base,                            // API-Basis-URL
        "model", model                           // gewähltes Modell
    );
  }
}
