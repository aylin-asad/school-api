package az.iktex.organization.structure.testders.request;
import jakarta.validation.constraints.*;
import az.iktex.organization.structure.testders.enums.ValidationMessage.Keys;
import az.iktex.organization.structure.testders.validation.StrongPassword;
public record ChangePasswordRequest(@NotBlank(message=Keys.REQUIRED) String oldPassword,@StrongPassword String newPassword){@Override public String toString(){return "ChangePasswordRequest[oldPassword=***,newPassword=***]";}}
