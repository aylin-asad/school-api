package az.iktex.organization.structure.testders.exception;

import az.iktex.organization.structure.testders.enums.ErrorCode;
public class InvalidRequestException extends BaseException {
    public InvalidRequestException(ErrorCode code, Object... args) { super(code, args); }
}
