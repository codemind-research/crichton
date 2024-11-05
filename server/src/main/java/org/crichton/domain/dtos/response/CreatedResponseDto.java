package org.crichton.domain.dtos.response;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public record CreatedResponseDto(UUID id) {

    public static Schema<?> getSchema(MessageSource messageSource, Locale locale) {
        return new Schema<>()
                .addProperty("id", new Schema<>().type("string").format("uuid")
                        .description(messageSource.getMessage("projectStatusResponse.id", null, locale)));
    }
}
