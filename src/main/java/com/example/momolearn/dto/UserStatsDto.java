package com.example.momolearn.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserStatsDto {
  private int xp;
  private int level;
  private int streak;
  private long correctAnswers;
}
