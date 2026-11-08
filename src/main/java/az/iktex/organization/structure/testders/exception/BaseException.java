package az.iktex.organization.structure.testders.exception;

import az.iktex.organization.structure.testders.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import java.util.Arrays;

public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object[] args;
    protected BaseException(ErrorCode code, Object... args) {
        super(code.getCode() + " " + code.getMessageKey());
        this.errorCode = code;
        this.args = Arrays.stream(args).map(String::valueOf).toArray();
    }
    public ErrorCode getErrorCode() { return errorCode; }
    public Object[] getArgs() { return args.clone(); }
    public HttpStatus getStatus() { return errorCode.getStatus(); }
}
