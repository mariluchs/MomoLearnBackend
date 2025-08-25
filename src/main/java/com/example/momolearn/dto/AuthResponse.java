package com.example.momolearn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
  private String userId;
  private String name;
  private String email;
  private String token; // Bearer-Token (opaque)
}
