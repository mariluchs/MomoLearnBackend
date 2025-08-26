# **MomoLearn – Backend**

Das Backend von **MomoLearn** ist die Basis der Plattform.  
Es kümmert sich um Nutzerverwaltung, Kurse, Lernsets, Gamification und die KI-Integration,  
die aus PDFs automatisch Multiple-Choice-Fragen erstellt.

---

## **Hauptfunktionen**
- Nutzer registrieren, anmelden und authentifizieren  
- Kurse und Lernsets verwalten  
- PDFs hochladen und automatisch Fragen generieren  
- Punkte, Level und Streaks zur Motivation berechnen  
- Daten sicher in MongoDB speichern (inkl. Dateien über GridFS)

---

## **Technischer Aufbau**
- **Spring Boot** – REST-API und Server-Framework  
- **MongoDB + GridFS** – Daten- und Dateispeicher  
- **DeepSeek API** – KI für Fragen-Generierung  
- **Security** – Bearer-Token mit Ownership-Check  

---

## **Funktionsweise der KI-Integration**
1. PDF hochladen  
2. Text wird ausgelesen  
3. Text geht an die DeepSeek-API  
4. KI liefert JSON mit Fragen  
5. Fragen werden gespeichert und sind sofort verfügbar
