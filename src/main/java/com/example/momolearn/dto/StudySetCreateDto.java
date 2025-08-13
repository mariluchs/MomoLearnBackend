package com.example.momolearn.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StudySetCreateDto {
  @NotBlank
  private String title;
  @NotBlank
  private String uploadId;
}
