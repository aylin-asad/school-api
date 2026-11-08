package az.iktex.organization.structure.testders.request;

import jakarta.validation.constraints.*;
import az.iktex.organization.structure.testders.enums.ValidationMessage;


public record StudentRequest(@NotBlank(message = ValidationMessage.Keys.REQUIRED) String name, @NotBlank(message = ValidationMessage.Keys.REQUIRED) String surname, @NotNull(message = ValidationMessage.Keys.NOT_NULL) @Positive(message = ValidationMessage.Keys.POSITIVE) Integer age) {}
