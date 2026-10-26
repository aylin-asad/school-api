package az.iktex.organization.structure.testders.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record PassportRequest(@NotBlank String serialNumber, @NotNull LocalDate issueDate, @NotBlank String country) {}
