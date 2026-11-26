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
        return new OpenAPI().components(new Components().addSecuritySchemes("bearerAuth", new io.swagger.v3.oas.models.security.SecurityScheme().type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))).addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearerAuth")).info(new Info()
            .title("School API").version("3.0")
            .description("School management API with internationalized errors and tracing.\n\n" +
                "| Code | Error | HTTP |\n|---|---|---|\n" + codes));
    }
    @Bean public OpenApiCustomizer commonErrorDocumentation() {
        return api -> {
            if (api.getComponents() == null) api.setComponents(new Components());
            ModelConverters.getInstance().read(ErrorResponse.class).forEach(api.getComponents()::addSchemas);
            if (api.getPaths() == null) return;
            api.getPaths().forEach((url,path) -> path.readOperations().forEach(op -> {
                boolean publicAuth = java.util.Set.of("/api/auth/register","/api/auth/login","/api/auth/refresh").contains(url);
                if(publicAuth)op.setSecurity(java.util.List.of());
                op.addParametersItem(new Parameter().in("header").name("Accept-Language")
                    .description("Preferred language: az / eng / rus").required(false)
                    .schema(new StringSchema()._default("az")));
                op.addParametersItem(new Parameter().in("header").name("X-Trace-Id")
                    .description("Optional 8–64 character correlation ID").required(false)
                    .schema(new StringSchema()));
                for (String status : new String[]{"400","401","403","404","409","500"}) {
                    if (op.getResponses() != null && op.getResponses().containsKey(status)) continue;
                    op.getResponses().addApiResponse(status, new ApiResponse().description("Error " + status)
                        .content(new Content().addMediaType("application/json", new MediaType()
                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));
                }
            }));
        };
    }
}
