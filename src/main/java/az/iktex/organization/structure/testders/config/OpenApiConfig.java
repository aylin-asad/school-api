package az.iktex.organization.structure.testders.config;

import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.response.ErrorResponse;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.*;
import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
public class OpenApiConfig {
    @Bean public OpenAPI schoolOpenAPI() {
        String codes = Arrays.stream(ErrorCode.values())
            .map(c -> "| " + c.getCode() + " | " + c.name() + " | " + c.getStatus().value() + " |")
            .collect(Collectors.joining("\n"));
        return new OpenAPI().components(new Components()).info(new Info()
            .title("School API").version("3.0")
            .description("School management API with internationalized errors and tracing.\n\n" +
                "| Code | Error | HTTP |\n|---|---|---|\n" + codes));
    }
    @Bean public OpenApiCustomizer commonErrorDocumentation() {
        return api -> {
            if (api.getComponents() == null) api.setComponents(new Components());
            ModelConverters.getInstance().read(ErrorResponse.class).forEach(api.getComponents()::addSchemas);
            if (api.getPaths() == null) return;
            api.getPaths().values().forEach(path -> path.readOperations().forEach(op -> {
                op.addParametersItem(new Parameter().in("header").name("Accept-Language")
                    .description("Preferred language: az / eng / rus").required(false)
                    .schema(new StringSchema()._default("az")));
                op.addParametersItem(new Parameter().in("header").name("X-Trace-Id")
                    .description("Optional 8–64 character correlation ID").required(false)
                    .schema(new StringSchema()));
                for (String status : new String[]{"400","404","409","500"}) {
                    if (op.getResponses() != null && op.getResponses().containsKey(status)) continue;
                    op.getResponses().addApiResponse(status, new ApiResponse().description("Error " + status)
                        .content(new Content().addMediaType("application/json", new MediaType()
                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));
                }
            }));
        };
    }
}
