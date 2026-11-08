package az.iktex.organization.structure.testders.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AzPhoneNumberValidator implements ConstraintValidator<AzPhoneNumber, String> {
    @Override public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;
        return value.replaceAll("\\s+", "").matches("\\+994(?:50|51|55|70|77|99)\\d{7}");
    }
}
