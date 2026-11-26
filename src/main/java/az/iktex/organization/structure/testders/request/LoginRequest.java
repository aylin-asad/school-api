package az.iktex.organization.structure.testders.request;
import jakarta.validation.constraints.*;
import az.iktex.organization.structure.testders.enums.ValidationMessage.Keys;
public record LoginRequest(@NotBlank(message=Keys.REQUIRED) String login,@NotBlank(message=Keys.REQUIRED) String password){@Override public String toString(){return "LoginRequest[login="+login+",password=***]";}}
