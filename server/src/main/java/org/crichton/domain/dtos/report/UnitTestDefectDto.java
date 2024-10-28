package org.crichton.domain.dtos.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Locale;

@Getter
@Builder
public class UnitTestDefectDto {

    private String file;

    @JsonProperty("function_name")
    private String functionName;

    @JsonProperty("defect_code")
    private String defectCode;


    static Schema<?> getSchema(MessageSource messageSource, Locale locale) {
        return new Schema<>()
                .addProperty("file", new Schema<>().type("string").description(messageSource.getMessage("responseReport.unitTestDefect.file", null, locale)))
                .addProperty("function_name", new Schema<>().type("string").description(messageSource.getMessage("responseReport.unitTestDefect.functionName", null, locale)))
                .addProperty("defect_code", new Schema<>().type("string").description(messageSource.getMessage("responseReport.unitTestDefect.defectCode", null, locale)));
    }

}
