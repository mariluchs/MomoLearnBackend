package com.example.momolearn;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Ein einfacher Test-Runner, der beim Starten der Anwendung ausgeführt wird.
 *
 * Ziel:
 * - Verbindung zur MongoDB prüfen
 * - Sicherstellen, dass die Anwendung korrekt auf die Datenbank zugreifen kann
 */
@Component
public class DatabaseTestRunner implements CommandLineRunner {

    private final MongoClient mongoClient;

    // Konstruktor-Injection des MongoDB-Clients
    public DatabaseTestRunner(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    /**
     * Wird automatisch nach dem Start der Anwendung ausgeführt.
     *
     * @param args Kommandozeilen-Argumente (hier ungenutzt)
     */
    @Override
    public void run(String... args) {
        // Verbindung zur konfigurierten Datenbank "learnapp" herstellen
        MongoDatabase db = mongoClient.getDatabase("learnapp");

        // Erfolgsmeldung in der Konsole ausgeben
        System.out.println("Verbindung zu MongoDB erfolgreich: " + db.getName());
    }
}
