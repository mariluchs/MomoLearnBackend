package com.example.momolearn.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class PdfTextService {

  public String extractText(InputStream in) throws IOException {
    try (PDDocument doc = PDDocument.load(in)) {
      PDFTextStripper stripper = new PDFTextStripper();
      stripper.setSortByPosition(true);
      String raw = stripper.getText(doc);
      return normalize(raw);
    }
  }

  private String normalize(String s) {
    s = s.replaceAll("-\\s*\\r?\\n\\s*", "");  // Silbentrennungen am Zeilenende
    s = s.replaceAll("\\r?\\n+", " ");        // Zeilenumbrüche zu Leerzeichen
    s = s.replaceAll("\\s{2,}", " ").trim();  // Whitespace normalisieren
    return s;
  }
}
