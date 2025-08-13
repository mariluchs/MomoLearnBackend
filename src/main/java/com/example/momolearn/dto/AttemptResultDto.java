package com.example.momolearn.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AttemptResultDto {
  private boolean correct;
  private int xpAwarded;
  private int newUserXp;
  private int newUserLevel;
  private int streak;
}
