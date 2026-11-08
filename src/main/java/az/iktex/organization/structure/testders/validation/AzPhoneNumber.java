package az.iktex.organization.structure.testders.validation;

import az.iktex.organization.structure.testders.enums.ValidationMessage;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=AzPhoneNumberValidator.class)
public @interface AzPhoneNumber {
    String message() default ValidationMessage.Keys.AZ_PHONE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
