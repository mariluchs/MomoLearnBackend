package com.example.momolearn.controller;

import com.example.momolearn.model.UploadDoc;
import com.example.momolearn.service.UploadService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * REST-Controller zum Hochladen und Verwalten von Dokumenten (z. B. PDF-Dateien),
 * die später z. B. zur KI-gestützten Fragengenerierung genutzt werden können.
 *
 * Basisroute: /users/{userId}/uploads
 */
@RestController
@RequestMapping("/users/{userId}/uploads")
public class UploadController {

  // Service, der die Logik für Speicherung, Verwaltung und Löschung von Uploads übernimmt
  private final UploadService uploads;

  public UploadController(UploadService uploads) { 
    this.uploads = uploads; 
  }

  /**
   * Hochladen einer PDF-Datei für einen bestimmten Benutzer.
   *
   * Endpoint: POST /users/{userId}/uploads
   * Content-Type: multipart/form-data
   *
   * Ablauf:
   * 1. Prüft, ob die Datei nicht leer ist und ob es sich um eine PDF handelt.
   * 2. Übergibt die Datei an den UploadService, der sie speichert.
   * 3. Gibt als Antwort die ID des gespeicherten Uploads zurück.
   *
   * @param userId ID des Nutzers, dem die Datei zugeordnet wird
   * @param file   die hochgeladene PDF-Datei
   * @return HTTP 201 mit JSON-Body wie: { "uploadId": "<ID>" }
   */
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Map<String,String>> upload(@PathVariable String userId,
                                                   @RequestPart("file") MultipartFile file)
      throws IOException {

    // Validierung: Datei darf nicht leer sein und muss PDF sein
    if (file.isEmpty() || !"application/pdf".equalsIgnoreCase(file.getContentType())) {
      return ResponseEntity.badRequest().body(Map.of("error", "Bitte eine PDF-Datei hochladen."));
    }

    // Datei speichern und Upload-Dokument (mit ID) zurückbekommen
    UploadDoc u = uploads.store(userId, file);

    // Rückgabe: HTTP 201 Created mit ID des gespeicherten Uploads
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("uploadId", u.getId()));
  }

  /**
   * Löschen eines hochgeladenen Dokuments.
   *
   * Endpoint: DELETE /users/{userId}/uploads/{uploadId}
   *
   * Ablauf:
   * 1. Optional könnte geprüft werden, ob das Dokument wirklich dem Nutzer gehört
   *    (Ownership-Check kann im Service implementiert sein).
   * 2. Dokument wird durch den UploadService gelöscht.
   *
   * Rückgabe: HTTP 204 No Content
   */
  @DeleteMapping("/{uploadId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String userId, @PathVariable String uploadId) {
    // Löscht den Upload anhand der ID
    uploads.delete(uploadId);
  }
}
