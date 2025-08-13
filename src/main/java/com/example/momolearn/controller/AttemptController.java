package com.example.momolearn.controller;

import com.example.momolearn.dto.AttemptRequest;
import com.example.momolearn.dto.AttemptResultDto;
import com.example.momolearn.service.GamificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/questions/{questionId}/attempts")
public class AttemptController {

  private final GamificationService service;

  public AttemptController(GamificationService service) {
    this.service = service;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AttemptResultDto attempt(@PathVariable String userId,
                                  @PathVariable String questionId,
                                  @Valid @RequestBody AttemptRequest req) {
    return service.recordAttempt(userId, questionId, req.getChosenIndex());
  }
}
