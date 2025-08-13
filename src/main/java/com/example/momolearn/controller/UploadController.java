package com.example.momolearn.controller;

import com.example.momolearn.model.UploadDoc;
import com.example.momolearn.service.UploadService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/users/{userId}/uploads")
public class UploadController {

  private final UploadService uploads;
  public UploadController(UploadService uploads) { this.uploads = uploads; }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Map<String,String>> upload(@PathVariable String userId, @RequestPart("file") MultipartFile file)
      throws IOException {
    if (file.isEmpty() || !"application/pdf".equalsIgnoreCase(file.getContentType())) {
      return ResponseEntity.badRequest().body(Map.of("error", "Bitte eine PDF-Datei hochladen."));
    }
    UploadDoc u = uploads.store(userId, file);
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("uploadId", u.getId()));
  }

  @DeleteMapping("/{uploadId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String userId, @PathVariable String uploadId) {
    // (optional) Ownership prüfen – wir speichern sie bereits im Document
    uploads.delete(uploadId);
  }
}
