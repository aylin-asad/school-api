package az.iktex.organization.structure.testders.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
    @Schema(description="HTTP status", example="404") int status,
    @Schema(description="Stable error identifier", example="ERR-2000") String code,
    @Schema(description="Localized error message", example="Student not found: 99999") String message,
    @Schema(description="Field validation errors") Map<String,String> errors,
    @Schema(description="Correlates response with logs", example="test-12345") String traceId,
    @Schema(description="Time of error", example="2026-11-03T09:58:08") LocalDateTime timestamp
) {}
