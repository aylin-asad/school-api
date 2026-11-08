package az.iktex.organization.structure.testders.request;

import jakarta.validation.constraints.*;
import az.iktex.organization.structure.testders.enums.ValidationMessage;


public record AuthorRequest(@NotBlank(message = ValidationMessage.Keys.REQUIRED) String name, @NotBlank(message = ValidationMessage.Keys.REQUIRED) String surname, String country) {}
