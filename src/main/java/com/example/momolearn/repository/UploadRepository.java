package com.example.momolearn.repository;

import com.example.momolearn.model.UploadDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository-Interface für die MongoDB-Collection "uploads".
 *
 * Stellt Standard-CRUD-Operationen für Upload-Dokumente zur Verfügung.
 * 
 * Da keine zusätzlichen Methoden definiert sind, können die
 * generischen Methoden von MongoRepository verwendet werden:
 * - save()
 * - findById()
 * - findAll()
 * - deleteById()
 * - count()
 */
public interface UploadRepository extends MongoRepository<UploadDoc, String> {
    // Keine zusätzlichen Methoden erforderlich – MongoRepository stellt alles Nötige bereit.
}
