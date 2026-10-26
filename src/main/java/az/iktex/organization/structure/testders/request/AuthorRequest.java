package az.iktex.organization.structure.testders.request;

import jakarta.validation.constraints.*;


public record AuthorRequest(@NotBlank String name, @NotBlank String surname, String country) {}
