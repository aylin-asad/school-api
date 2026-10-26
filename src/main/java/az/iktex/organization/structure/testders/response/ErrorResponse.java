package az.iktex.organization.structure.testders.response;

import java.time.Instant;
public record ErrorResponse(Instant timestamp, int status, String message) {}
