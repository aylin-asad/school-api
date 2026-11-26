package az.iktex.organization.structure.testders.request;
import az.iktex.organization.structure.testders.enums.Role;
import az.iktex.organization.structure.testders.enums.ValidationMessage.Keys;
import jakarta.validation.constraints.NotNull;
public record ChangeRoleRequest(@NotNull(message=Keys.NOT_NULL) Role role){}
