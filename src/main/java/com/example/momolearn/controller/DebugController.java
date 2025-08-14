// src/main/java/com/example/momolearn/controller/DebugController.java
package com.example.momolearn.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
  @GetMapping("/deepseek")
  public Map<String,Object> deepseek(
      @Value("${deepseek.api.key:${DEEPSEEK_API_KEY:}}") String key,
      @Value("${deepseek.api.base-url:${deepseek.api.base:https://api.deepseek.com}}") String base,
      @Value("${deepseek.api.model:${deepseek.model:deepseek-chat}}") String model
  ) {
    return Map.of("hasKey", key != null && !key.isBlank(), "base", base, "model", model);
  }
}
