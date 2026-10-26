package az.iktex.organization.structure.testders.request;

import jakarta.validation.constraints.*;


public record DepartmentRequest(@NotBlank String name, String location, String phone) {}
