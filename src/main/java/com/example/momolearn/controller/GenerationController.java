package com.example.momolearn.controller;

import com.example.momolearn.model.StudySet;
import com.example.momolearn.service.StudySetService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users/{userId}/sets/{setId}")
public class GenerationController {

  private final StudySetService service;
  public GenerationController(StudySetService service) { this.service = service; }

  @PostMapping("/generate")
  public Map<String,Object> generate(@PathVariable String userId,
                                     @PathVariable String setId,
                                     @RequestParam(defaultValue = "5") int count) {
    int created = service.generateQuestions(userId, setId, count);
    return Map.of("created", created, "status", "READY");
  }

  @GetMapping
  public StudySet get(@PathVariable String userId, @PathVariable String setId) {
    StudySet set = service.get(setId);
    if (!userId.equals(set.getUserId())) {
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.FORBIDDEN, "Set does not belong to user");
    }
    return set;
  }
}
