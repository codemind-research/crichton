package org.crichton.configuration.swagger;

import io.swagger.models.Swagger;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.crichton.domain.dtos.report.ResponseReportDto;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Locale;

class ReportApi {

    static final String BASE_URL = String.format("%s/report", SwaggerConfig.BASE_API_URL_V1);

    static final String GET_REPORT_URL = String.format("%s/{id}", BASE_URL);

    static Operation getReportOperation(MessageSource messageSource, Locale locale) {
        return new Operation()
                .summary(messageSource.getMessage("report.summary", null, locale))
                .description(messageSource.getMessage("report.description", null, locale))
                .parameters(List.of(new io.swagger.v3.oas.models.parameters.Parameter()
                        .name("id")
                        .in("path")
                        .required(true)
                        .description(messageSource.getMessage("parameter.id.description", null, locale))))
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse()
                                .description(messageSource.getMessage("report.response.200", null, locale))
                                .content(new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE, new io.swagger.v3.oas.models.media.MediaType().schema(ResponseReportDto.getSchema(messageSource, locale))))
                        )
                        .addApiResponse("404", new ApiResponse().description(messageSource.getMessage("default.response.404", null, locale)))
                        .addApiResponse("400", new ApiResponse().description(messageSource.getMessage("report.response.400", null, locale)))
                        .addApiResponse("500", new ApiResponse().description(messageSource.getMessage("default.response.500", null, locale)))
                );
    }
}
