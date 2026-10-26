package az.iktex.organization.structure.testders.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record EmployeeRequest(@NotBlank String name, @NotBlank String surname, @NotNull @Positive BigDecimal salary, @NotNull Long departmentId) {}
