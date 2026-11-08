package az.iktex.organization.structure.testders.exception;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    private ResponseEntity<ErrorResponse> build(HttpServletRequest request, Exception ex, ErrorCode code,
            Map<String,String> errors, Object... args) {
        HttpStatus status = code.getStatus();
        if (status.is5xxServerError())
            log.error("Request failed: {} {} -> {} {} ({}) [{}] args={}", request.getMethod(),
                request.getRequestURI(), status.value(), code.getCode(), code.getMessageKey(),
                ex.getClass().getSimpleName(), Arrays.toString(args), ex);
        else
            log.warn("Request failed: {} {} -> {} {} ({}) [{}] args={} errors={}", request.getMethod(),
                request.getRequestURI(), status.value(), code.getCode(), code.getMessageKey(),
                ex.getClass().getSimpleName(), Arrays.toString(args), errors);
        String message = messageSource.getMessage(code.getMessageKey(), args, code.getMessageKey(), LocaleContextHolder.getLocale());
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), code.getCode(),
            message, errors, MDC.get("traceId"), LocalDateTime.now()));
    }
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> custom(HttpServletRequest req, BaseException ex) {
        return build(req, ex, ex.getErrorCode(), Map.of(), ex.getArgs());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(HttpServletRequest req, MethodArgumentNotValidException ex) {
        Map<String,String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.putIfAbsent(e.getField(),
            e.getDefaultMessage() == null ? "" : e.getDefaultMessage()));
        return build(req, ex, ErrorCode.VALIDATION_ERROR, errors);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> malformed(HttpServletRequest req, HttpMessageNotReadableException ex) {
        return build(req, ex, ErrorCode.MALFORMED_REQUEST, Map.of());
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> mismatch(HttpServletRequest req, MethodArgumentTypeMismatchException ex) {
        return build(req, ex, ErrorCode.TYPE_MISMATCH, Map.of(), ex.getName());
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> missing(HttpServletRequest req, MissingServletRequestParameterException ex) {
        return build(req, ex, ErrorCode.MISSING_PARAMETER, Map.of(), ex.getParameterName());
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> method(HttpServletRequest req, HttpRequestMethodNotSupportedException ex) {
        return build(req, ex, ErrorCode.METHOD_NOT_SUPPORTED, Map.of());
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> resource(HttpServletRequest req, NoResourceFoundException ex) {
        return build(req, ex, ErrorCode.URL_NOT_FOUND, Map.of());
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integrity(HttpServletRequest req, DataIntegrityViolationException ex) {
        return build(req, ex, ErrorCode.DATA_INTEGRITY_VIOLATION, Map.of());
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> legacyNotFound(HttpServletRequest req, EntityNotFoundException ex) {
        return build(req, ex, ErrorCode.URL_NOT_FOUND, Map.of());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> legacyBadRequest(HttpServletRequest req, IllegalArgumentException ex) {
        return build(req, ex, ErrorCode.MALFORMED_REQUEST, Map.of());
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> legacyConflict(HttpServletRequest req, IllegalStateException ex) {
        return build(req, ex, ErrorCode.DATA_INTEGRITY_VIOLATION, Map.of());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> other(HttpServletRequest req, Exception ex) {
        return build(req, ex, ErrorCode.INTERNAL_ERROR, Map.of());
    }
}
