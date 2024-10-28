package org.crichton.configuration.swagger;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.crichton.domain.dtos.response.ProjectStatusResponseDto;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Locale;


 class ProjectApi {

     static final String BASE_URL = String.format("%s/project", SwaggerConfig.BASE_API_URL_V1);

     static final String ANALYSIS_RUN_API = String.format("%s/run", BASE_URL);
     static final String GET_STATUS_API = String.format("%s/status/{id}", BASE_URL);
     static final String DELETE_PROJECT_API = String.format("%s/remove/{id}", BASE_URL);


     static Operation getCreateOperation(MessageSource messageSource, Locale locale) {

         // Define each field with its own Schema and localized descriptions
         Schema<?> sourceCodeSchema = new Schema<>()
                 .type("string")
                 .format("binary")
                 .description(messageSource.getMessage("project.sourceCode.description", null, locale));

         Schema<?> testSpecFileSchema = new Schema<>()
                 .type("string")
                 .format("binary")
                 .description(messageSource.getMessage("project.testSpecFile.description", null, locale));

         Schema<?> defectSpecFileSchema = new Schema<>()
                 .type("string")
                 .format("binary")
                 .description(messageSource.getMessage("project.defectSpecFile.description", null, locale));

         Schema<?> safeSpecFileSchema = new Schema<>()
                 .type("string")
                 .format("binary")
                 .description(messageSource.getMessage("project.safeSpecFile.description", null, locale));

         Schema<?> unitTestSpecFileSchema = new Schema<>()
                 .type("string")
                 .format("binary")
                 .description(messageSource.getMessage("project.unitTestSpecFile.description", null, locale))
                 .nullable(true);

         // Combine individual schemas into one schema for the request body
         var requestSchema = new Schema<>()
                 .addProperties("sourceCode", sourceCodeSchema)
                 .addProperties("testSpecFile", testSpecFileSchema)
                 .addProperties("defectSpecFile", defectSpecFileSchema)
                 .addProperties("safeSpecFile", safeSpecFileSchema)
                 .addProperties("unitTestSpecFile", unitTestSpecFileSchema);

        return new Operation()
                .summary(messageSource.getMessage("project.create.summary", null, locale))
                .description(messageSource.getMessage("project.create.description", null, locale))
                .requestBody(new RequestBody()
                        .description(messageSource.getMessage("project.create.requestBody", null, locale))
                        .content(new Content().addMediaType("multipart/form-data", new io.swagger.v3.oas.models.media.MediaType().schema(requestSchema))))
                .responses(new ApiResponses()
                        .addApiResponse("201", new ApiResponse().description(messageSource.getMessage("project.response.201", null, locale)))
                        .addApiResponse("400", new ApiResponse().description(messageSource.getMessage("project.response.400", null, locale)))
                        .addApiResponse("500", new ApiResponse().description(messageSource.getMessage("project.response.500", null, locale)))
                );
    }

     static Operation getStatusOperation(MessageSource messageSource, Locale locale) {

        return new Operation()
                .summary(messageSource.getMessage("project.status.summary", null, locale))
                .description(messageSource.getMessage("project.status.description", null, locale))
                .parameters(List.of(new io.swagger.v3.oas.models.parameters.Parameter()
                        .name("id")
                        .in("path")
                        .required(true)
                        .description(messageSource.getMessage("project.id.description", null, locale))))
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse()
                                .description(messageSource.getMessage("project.status.response.200", null, locale))
                                .content(new io.swagger.v3.oas.models.media.Content()
                                            .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                                new io.swagger.v3.oas.models.media.MediaType().schema(ProjectStatusResponseDto.getSchema(messageSource, locale))
                                        )
                                    )
                        )
                        .addApiResponse("404", new ApiResponse().description(messageSource.getMessage("project.status.response.404", null, locale)))
                        .addApiResponse("500", new ApiResponse().description(messageSource.getMessage("project.status.response.500", null, locale))));

    }

     static Operation getDeleteOperation(MessageSource messageSource, Locale locale) {

        return new Operation()
                .summary(messageSource.getMessage("project.delete.summary", null, locale))
                .description(messageSource.getMessage("project.delete.description", null, locale))
                .parameters(List.of(new io.swagger.v3.oas.models.parameters.Parameter()
                        .name("id")
                        .in("path")
                        .required(true)
                        .description(messageSource.getMessage("project.id.description", null, locale))))
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse().description(messageSource.getMessage("project.delete.response.200", null, locale)))
                        .addApiResponse("404", new ApiResponse().description(messageSource.getMessage("project.delete.response.404", null, locale)))
                        .addApiResponse("500", new ApiResponse().description(messageSource.getMessage("project.delete.response.500", null, locale))));

    }

}