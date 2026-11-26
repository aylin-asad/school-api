package az.iktex.organization.structure.testders.validation;
import jakarta.validation.*;
import java.lang.annotation.*;
import az.iktex.organization.structure.testders.enums.ValidationMessage;
@Documented @Constraint(validatedBy=StrongPasswordValidator.class) @Target({ElementType.FIELD,ElementType.PARAMETER,ElementType.RECORD_COMPONENT,ElementType.ANNOTATION_TYPE}) @Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
 String message() default ValidationMessage.Keys.PASSWORD_STRONG;
 Class<?>[] groups() default {}; Class<? extends Payload>[] payload() default {};
}
