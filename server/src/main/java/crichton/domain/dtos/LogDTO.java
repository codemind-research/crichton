package crichton.domain.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class LogDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class LogRequest{
        @Schema(description = "최대 길이")
        private long maxline;
        @Schema(description = "시작 위치")
        private int startpos;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LogResponse{
        @Schema(description = "범위 이탈을 체크하는 기능")
        private Boolean overflow;
        @Schema(description = "끝나는 위치")
        private long endpos;
        @Schema(description = "로그")
        private StringBuilder text;
    }

}
