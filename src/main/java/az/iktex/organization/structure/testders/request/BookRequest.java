package az.iktex.organization.structure.testders.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record BookRequest(@NotBlank String title, @NotNull @PositiveOrZero BigDecimal price, @NotNull @Positive Integer pageCount) {}
