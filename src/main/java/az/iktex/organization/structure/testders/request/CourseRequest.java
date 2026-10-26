package az.iktex.organization.structure.testders.request;

import jakarta.validation.constraints.*;


public record CourseRequest(@NotBlank String title, @NotNull @Positive Integer credit, @NotBlank String teacher) {}
