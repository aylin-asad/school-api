package az.iktex.organization.structure.testders.request;

import jakarta.validation.constraints.*;


public record StudentRequest(@NotBlank String name, @NotBlank String surname, @NotNull @Positive Integer age) {}
