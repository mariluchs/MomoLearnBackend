package com.example.momolearn.controller;

import com.example.momolearn.dto.UserStatsDto;
import com.example.momolearn.service.GamificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/stats")
public class UserStatsController {

  private final GamificationService service;

  public UserStatsController(GamificationService service) {
    this.service = service;
  }

  @GetMapping
  public UserStatsDto get(@PathVariable String userId) {
    return service.stats(userId);
  }
}
