package com.example.momolearn.controller;

import com.example.momolearn.model.Question;
import com.example.momolearn.model.StudySet;
import com.example.momolearn.repository.QuestionRepository;
import com.example.momolearn.service.StudySetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/sets/{setId}/questions") // /api kommt aus application.properties
public class QuestionController {

  private final QuestionRepository repo;
  private final StudySetService sets;

  public QuestionController(QuestionRepository repo, StudySetService sets) {
    this.repo = repo;
    this.sets = sets;
  }

  @GetMapping
  public List<Question> list(@PathVariable String userId, @PathVariable String setId) {
    // Ownership prüfen
    StudySet s = sets.get(setId);
    if (s == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lern-Set nicht gefunden");
    }
    if (!userId.equals(s.getUserId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Set gehört nicht zum Nutzer");
    }

    // Fragen zu diesem Set zurückgeben (nach studySetId)
    return repo.findAllByStudySetId(setId);
  }
}
