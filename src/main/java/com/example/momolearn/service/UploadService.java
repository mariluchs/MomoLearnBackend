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

@Service
public class UploadService {

  private final GridFsTemplate gridFs;
  private final UploadRepository uploads;

  public UploadService(GridFsTemplate gridFs, UploadRepository uploads) {
    this.gridFs = gridFs;
    this.uploads = uploads;
  }

  public UploadDoc store(String userId, MultipartFile file) throws IOException {
    final String filename = Optional.ofNullable(file.getOriginalFilename()).orElse("upload.bin");
    final String contentType = Optional.ofNullable(file.getContentType()).orElse("application/octet-stream");

    ObjectId oid;
    try (InputStream in = file.getInputStream()) {
      oid = gridFs.store(in, filename, contentType);
    }

    UploadDoc doc = UploadDoc.builder()
        .userId(userId)
        .filename(filename)
        .contentType(contentType)
        .size(file.getSize())
        .storageId(oid.toHexString())
        .uploadedAt(Instant.now())
        .build();

    return uploads.save(doc);
  }

  public InputStream openStream(UploadDoc u) throws IOException {
    ObjectId oid = toObjectIdOrNotFound(u.getStorageId());

    GridFSFile file = Optional.ofNullable(
        gridFs.findOne(query(where("_id").is(oid)))
    ).orElseThrow(() -> new FileNotFoundException("GridFS id not found: " + u.getStorageId()));

    GridFsResource res = gridFs.getResource(file);
    return res.getInputStream();
  }

  public void delete(String uploadId) {
    uploads.findById(uploadId).ifPresent(u -> {
      try {
        ObjectId oid = toObjectIdOrNotFound(u.getStorageId());
        gridFs.delete(query(where("_id").is(oid)));
      } catch (IOException ignore) {
        // Ungültige/fehlende GridFS-ID: Datei existiert nicht -> wir löschen nur das Metadokument
      }
      uploads.deleteById(uploadId);
    });
  }

  private ObjectId toObjectIdOrNotFound(String id) throws FileNotFoundException {
    try {
      return new ObjectId(id);
    } catch (IllegalArgumentException e) {
      throw new FileNotFoundException("Invalid GridFS id: " + id);
    }
  }
}
