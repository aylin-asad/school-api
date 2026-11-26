package az.iktex.organization.structure.testders.request;
import jakarta.validation.constraints.*;
import az.iktex.organization.structure.testders.enums.ValidationMessage.Keys;
public record RefreshRequest(@NotBlank(message=Keys.REQUIRED) String refreshToken){@Override public String toString(){return "RefreshRequest[refreshToken=***]";}}
