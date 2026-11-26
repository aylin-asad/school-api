package az.iktex.organization.structure.testders.request;
import jakarta.validation.constraints.*;
import az.iktex.organization.structure.testders.enums.ValidationMessage.Keys;
import az.iktex.organization.structure.testders.validation.StrongPassword;
public record RegisterRequest(@NotBlank(message=Keys.REQUIRED) @Size(min=3,max=50,message=Keys.SIZE_MIN) @Pattern(regexp="[A-Za-z0-9._]+",message=Keys.USERNAME_PATTERN) String username,
 @NotBlank(message=Keys.REQUIRED) @Email(message=Keys.EMAIL) @Size(max=100,message=Keys.SIZE_MAX) String email,
 @StrongPassword String password){ @Override public String toString(){return "RegisterRequest[username="+username+", password=***]";} }
