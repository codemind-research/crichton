package org.crichton.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class LogDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class LogRequest{
        @Schema(description = "Maximum Length")
        private long maxline;
        @Schema(description = "Start Position")
        private int startpos;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LogResponse{
        @Schema(description = "Checking for Range Overflow")
        private Boolean overflow;
        @Schema(description = "End Position")
        private long endpos;
        @Schema(description = "log")
        private StringBuilder text;
    }

}
