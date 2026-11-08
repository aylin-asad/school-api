package az.iktex.organization.structure.testders.request;

import jakarta.validation.constraints.*;
import az.iktex.organization.structure.testders.enums.ValidationMessage;
import java.math.BigDecimal;

public record BookRequest(@NotBlank(message = ValidationMessage.Keys.REQUIRED) String title, @NotNull(message = ValidationMessage.Keys.NOT_NULL) @PositiveOrZero(message = ValidationMessage.Keys.POSITIVE_OR_ZERO) BigDecimal price, @NotNull(message = ValidationMessage.Keys.NOT_NULL) @Positive(message = ValidationMessage.Keys.POSITIVE) Integer pageCount) {}
