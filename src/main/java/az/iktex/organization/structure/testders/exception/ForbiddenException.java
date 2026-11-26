package az.iktex.organization.structure.testders.exception;
import az.iktex.organization.structure.testders.enums.ErrorCode;
public class ForbiddenException extends BaseException { public ForbiddenException(ErrorCode code,Object... args){super(code,args);} }
