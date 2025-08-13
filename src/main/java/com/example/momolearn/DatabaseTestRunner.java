package com.example.momolearn;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

@Component
public class DatabaseTestRunner implements CommandLineRunner {

    private final MongoClient mongoClient;

    public DatabaseTestRunner(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public void run(String... args) {
        MongoDatabase db = mongoClient.getDatabase("learnapp");
        System.out.println("Verbindung zu MongoDB erfolgreich: " + db.getName());
    }
}