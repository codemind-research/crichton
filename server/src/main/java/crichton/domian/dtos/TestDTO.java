package crichton.domian.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class TestDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestRequest {
        @Schema(description = "테스트할 소스코드 경로", required = true)
        private String sourcePath;
    }

    @Getter
    @Builder
    public static class TestResponse {
        @Schema(description = "Cli 에서 Output 경로", required = true)
        private String reportPath;
    }
}
