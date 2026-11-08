package az.iktex.organization.structure.testders.request;

import jakarta.validation.constraints.*;
import az.iktex.organization.structure.testders.enums.ValidationMessage;
import az.iktex.organization.structure.testders.validation.AzPhoneNumber;


public record DepartmentRequest(@NotBlank(message = ValidationMessage.Keys.REQUIRED) String name, String location, @AzPhoneNumber String phone) {}
