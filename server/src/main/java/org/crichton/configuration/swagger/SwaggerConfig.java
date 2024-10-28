package org.crichton.configuration.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;


/**
 Class responsible for generating Swagger API documentation.
 Intended for future use with the HTTPS protocol.
 Default URL: https://localhost:[port]/swagger-ui/index.html#/
 Adjust the port number or IP address accordingly.
 */
@OpenAPIDefinition(
        info = @Info(title = "Crichton API Documentation",
                description = "To understand the Swagger documentation and test the API, " +
                        "you need to first execute the GET METHOD /token API in the Auth Controller to obtain token information.<br>" +
                        " Then, click the Authorize button of the desired API, and enter the information of the obtained AccessToken.",
                version = "v1"))
@Configuration("Swagger")
public class SwaggerConfig {

    static final String BASE_API_URL_V1 = "/api/v1/crichton";

    private final MessageSource messageSource;

    public SwaggerConfig(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Bean
    public OpenAPI openAPI(){

        Locale locale = LocaleContextHolder.getLocale();

        var info = new io.swagger.v3.oas.models.info.Info()
                .title(messageSource.getMessage("swagger.title", null, "Crichton API", locale))
                .description(messageSource.getMessage("swagger.description", null, "Description of the Crichton API", locale))
                .version(messageSource.getMessage("swagger.version", null, "unknown", locale));

        return new OpenAPI()
                .info(info)
//#region Project API
                .path(ProjectApi.ANALYSIS_RUN_API, new PathItem().post(ProjectApi.getCreateOperation(messageSource, locale)))
                .path(ProjectApi.GET_STATUS_API, new PathItem().get(ProjectApi.getStatusOperation(messageSource, locale)))
                .path(ProjectApi.DELETE_PROJECT_API, new PathItem().delete(ProjectApi.getDeleteOperation(messageSource, locale)))
//#endregion

//#region Report API
                .path(ReportApi.GET_REPORT_URL, new PathItem().get(ReportApi.getReportOperation(messageSource, locale)));
//#endregion

    }

}
