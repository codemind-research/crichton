package org.crichton.domain.dtos.response;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Builder
public record ProjectStatusResponseDto(@NonNull UUID id, @NonNull String status) {

    @SuppressWarnings("unchekced")
    public static Schema<?> getSchema(MessageSource messageSource, Locale locale) {
        return new Schema<>()
                .addProperty("id", new Schema<>().type("string").format("uuid")
                        .description(messageSource.getMessage("projectStatusResponse.id", null, locale)))
                .addProperty("status", new StringSchema()
                        .description(messageSource.getMessage("projectStatusResponse.status", null, locale))
                        ._enum(List.of("none", "testing", "pass", "fail")));
    }

}
