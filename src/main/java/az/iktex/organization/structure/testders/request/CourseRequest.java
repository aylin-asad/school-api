package az.iktex.organization.structure.testders.request;

import jakarta.validation.constraints.*;
import az.iktex.organization.structure.testders.enums.ValidationMessage;


public record CourseRequest(@NotBlank(message = ValidationMessage.Keys.REQUIRED) String title, @NotNull(message = ValidationMessage.Keys.NOT_NULL) @Positive(message = ValidationMessage.Keys.POSITIVE) Integer credit, @NotBlank(message = ValidationMessage.Keys.REQUIRED) String teacher) {}
