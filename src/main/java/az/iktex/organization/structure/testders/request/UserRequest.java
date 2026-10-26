package az.iktex.organization.structure.testders.request;

import jakarta.validation.constraints.*;
import az.iktex.organization.structure.testders.enums.Gender;

public record UserRequest(@NotBlank String name, @NotBlank String surname, @NotBlank @Email String email, @NotNull Gender gender) {}
