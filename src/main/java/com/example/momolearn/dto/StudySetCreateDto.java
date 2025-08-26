package com.example.momolearn.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) für das Erstellen eines neuen StudySets (Lern-Sets).
 *
 * Wird typischerweise an den Controller geschickt bei:
 *   POST /users/{userId}/courses/{courseId}/sets
 */
@Getter
@Setter
public class StudySetCreateDto {

  /**
   * Titel des neuen StudySets.
   * - @NotBlank: Darf nicht leer oder nur aus Leerzeichen bestehen.
   */
  @NotBlank
  private String title;

  /**
   * ID des hochgeladenen Dokuments, das als Basis für dieses StudySet dient.
   * - @NotBlank: Muss angegeben sein, weil das Set mit einem Upload verknüpft wird.
   */
  @NotBlank
  private String uploadId;
}
