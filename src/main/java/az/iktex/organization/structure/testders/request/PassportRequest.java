package az.iktex.organization.structure.testders.request;

import jakarta.validation.constraints.*;
import az.iktex.organization.structure.testders.enums.ValidationMessage;
import java.time.LocalDate;

public record PassportRequest(@NotBlank(message = ValidationMessage.Keys.REQUIRED) String serialNumber, @NotNull(message = ValidationMessage.Keys.NOT_NULL) LocalDate issueDate, @NotBlank(message = ValidationMessage.Keys.REQUIRED) String country) {}
