package com.example.momolearn.service;

import com.example.momolearn.model.UploadDoc;
import com.example.momolearn.repository.UploadRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Service zum Speichern, Abrufen und Löschen von Uploads in MongoDB GridFS.
 *
 * Funktioniert zusammen mit:
 * - UploadRepository: Metadaten über die hochgeladenen Dateien
 * - GridFsTemplate: Physische Speicherung im GridFS
 */
@Service
public class UploadService {

  private final GridFsTemplate gridFs;
  private final UploadRepository uploads;

  public UploadService(GridFsTemplate gridFs, UploadRepository uploads) {
    this.gridFs = gridFs;
    this.uploads = uploads;
  }

  /**
   * Speichert eine hochgeladene Datei im GridFS und legt ein Metadaten-Dokument an.
   *
   * @param userId ID des Benutzers, dem die Datei gehört
   * @param file   Hochgeladene Datei
   * @return gespeichertes UploadDoc mit Metadaten
   */
  public UploadDoc store(String userId, MultipartFile file) throws IOException {
    final String filename = Optional.ofNullable(file.getOriginalFilename()).orElse("upload.bin");
    final String contentType = Optional.ofNullable(file.getContentType()).orElse("application/octet-stream");

    // Datei in GridFS speichern
    ObjectId oid;
    try (InputStream in = file.getInputStream()) {
      oid = gridFs.store(in, filename, contentType);
    }

    // Metadokument speichern
    UploadDoc doc = UploadDoc.builder()
        .userId(userId)
        .filename(filename)
        .contentType(contentType)
        .size(file.getSize())
        .storageId(oid.toHexString()) // Referenz zur GridFS-Datei
        .uploadedAt(Instant.now())
        .build();

    return uploads.save(doc);
  }

  /**
   * Öffnet einen InputStream für den Inhalt einer gespeicherten Datei.
   *
   * @param u Metadatenobjekt des Uploads
   * @return InputStream zur Datei
   */
  public InputStream openStream(UploadDoc u) throws IOException {
    ObjectId oid = toObjectIdOrNotFound(u.getStorageId());

    // Datei in GridFS finden
    GridFSFile file = Optional.ofNullable(
        gridFs.findOne(query(where("_id").is(oid)))
    ).orElseThrow(() -> new FileNotFoundException("GridFS id not found: " + u.getStorageId()));

    GridFsResource res = gridFs.getResource(file);
    return res.getInputStream();
  }

  /**
   * Löscht sowohl die Datei im GridFS als auch das zugehörige Metadokument.
   *
   * @param uploadId ID des Upload-Metadokuments
   */
  public void delete(String uploadId) {
    uploads.findById(uploadId).ifPresent(u -> {
      try {
        // Datei im GridFS löschen
        ObjectId oid = toObjectIdOrNotFound(u.getStorageId());
        gridFs.delete(query(where("_id").is(oid)));
      } catch (IOException ignore) {
        // Wenn GridFS-Datei nicht existiert, wird nur das Metadokument gelöscht.
      }
      uploads.deleteById(uploadId);
    });
  }

  /**
   * Wandelt einen String in eine ObjectId um.
   *
   * @param id Hex-String der ObjectId
   * @return ObjectId
   * @throws FileNotFoundException falls die ID ungültig ist
   */
  private ObjectId toObjectIdOrNotFound(String id) throws FileNotFoundException {
    try {
      return new ObjectId(id);
    } catch (IllegalArgumentException e) {
      throw new FileNotFoundException("Invalid GridFS id: " + id);
    }
  }
}
