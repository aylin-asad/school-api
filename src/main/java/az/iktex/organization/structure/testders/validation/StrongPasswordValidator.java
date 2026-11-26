package az.iktex.organization.structure.testders.validation;
import jakarta.validation.*;
import java.nio.charset.StandardCharsets;
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword,String>{
 public boolean isValid(String s,ConstraintValidatorContext c){return s!=null && s.length()>=8 && s.getBytes(StandardCharsets.UTF_8).length<=72 && s.chars().anyMatch(Character::isUpperCase) && s.chars().anyMatch(Character::isLowerCase) && s.chars().anyMatch(Character::isDigit);}
}
