package com.example.momolearn.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AttemptRequest {
  @Min(0) @Max(3)
  private int chosenIndex;
}
