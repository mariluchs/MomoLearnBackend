package com.example.momolearn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Haupteinstiegspunkt der Anwendung.
 *
 * @SpringBootApplication:
 *  - Aktiviert Auto-Konfiguration
 *  - Aktiviert Komponenten-Scan (scannt alle Klassen im Package com.example.momolearn und Unterpakete)
 *  - Markiert die Klasse als Spring Boot Application
 */
@SpringBootApplication
public class MomoLearnApplication {

    /**
     * Startet die Spring Boot Anwendung.
     *
     * @param args Argumente, die beim Start übergeben werden (z.B. --server.port=8081)
     */
    public static void main(String[] args) {
        SpringApplication.run(MomoLearnApplication.class, args);
    }

}
