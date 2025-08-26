package com.example.momolearn.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Service-Klasse, um Text aus PDF-Dateien zu extrahieren.
 *
 * Wird verwendet, um hochgeladene PDFs (z. B. Skripte oder Dokumente)
 * in reinen Text zu konvertieren, der anschließend für die KI-Generierung
 * von Fragen genutzt wird.
 */
@Service
public class PdfTextService {

  /**
   * Liest den Text aus einem PDF-InputStream.
   *
   * @param in InputStream der PDF-Datei (z. B. aus GridFS oder Upload)
   * @return Extrahierter, bereinigter Text
   * @throws IOException Falls PDF nicht lesbar oder beschädigt ist
   */
  public String extractText(InputStream in) throws IOException {
    // PDF öffnen und automatisch schließen, sobald try-Block beendet wird
    try (PDDocument doc = PDDocument.load(in)) {
      PDFTextStripper stripper = new PDFTextStripper();
      stripper.setSortByPosition(true); // Text in logischer Reihenfolge extrahieren
      String raw = stripper.getText(doc);
      return normalize(raw); // Text bereinigen
    }
  }

  /**
   * Bereinigt den extrahierten Text:
   * - Entfernt Silbentrennungen am Zeilenende
   * - Ersetzt Zeilenumbrüche durch Leerzeichen
   * - Reduziert Mehrfach-Leerzeichen auf ein einzelnes
   *
   * @param s Rohtext aus dem PDF
   * @return Bereinigter, gut lesbarer Text
   */
  private String normalize(String s) {
    s = s.replaceAll("-\\s*\\r?\\n\\s*", "");  // Silbentrennungen am Zeilenende entfernen
    s = s.replaceAll("\\r?\\n+", " ");        // Zeilenumbrüche in Leerzeichen umwandeln
    s = s.replaceAll("\\s{2,}", " ").trim();  // Mehrfach-Whitespaces reduzieren
    return s;
  }
}
