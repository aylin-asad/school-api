package az.iktex.organization.structure.testders.request;

import jakarta.validation.constraints.*;
import az.iktex.organization.structure.testders.enums.ValidationMessage;
import az.iktex.organization.structure.testders.enums.Gender;

public record UserRequest(@NotBlank(message = ValidationMessage.Keys.REQUIRED) String name, @NotBlank(message = ValidationMessage.Keys.REQUIRED) String surname, @NotBlank(message = ValidationMessage.Keys.REQUIRED) @Email(message = ValidationMessage.Keys.EMAIL) String email, @NotNull(message = ValidationMessage.Keys.NOT_NULL) Gender gender) {}
