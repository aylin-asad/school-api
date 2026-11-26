package az.iktex.organization.structure.testders.exception;
import az.iktex.organization.structure.testders.enums.ErrorCode;
public class UnauthorizedException extends BaseException { public UnauthorizedException(ErrorCode code,Object... args){super(code,args);} }
