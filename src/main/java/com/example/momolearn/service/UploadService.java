package com.example.momolearn.service;

import com.example.momolearn.model.UploadDoc;
import com.example.momolearn.repository.UploadRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class UploadService {

  private final GridFsTemplate gridFs;
  private final UploadRepository uploads;

  public UploadService(GridFsTemplate gridFs, UploadRepository uploads) {
    this.gridFs = gridFs; this.uploads = uploads;
  }

  public UploadDoc store(String userId, MultipartFile file) throws IOException {
    String storageId = gridFs.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType()).toString();
    UploadDoc doc = UploadDoc.builder()
        .userId(userId)
        .filename(file.getOriginalFilename())
        .contentType(file.getContentType())
        .size(file.getSize())
        .storageId(storageId)
        .uploadedAt(java.time.Instant.now())
        .build();
    return uploads.save(doc);
  }

  public InputStream openStream(UploadDoc u) throws IOException {
    GridFSFile file = gridFs.findOne(query(where("_id").is(new ObjectId(u.getStorageId()))));
    if (file == null) throw new FileNotFoundException("GridFS id not found: " + u.getStorageId());
    GridFsResource res = gridFs.getResource(file);
    return res.getInputStream();
  }

  public void delete(String uploadId) {
    uploads.findById(uploadId).ifPresent(u -> {
      gridFs.delete(query(where("_id").is(new ObjectId(u.getStorageId()))));
      uploads.deleteById(uploadId);
    });
  }
}
