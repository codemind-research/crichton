package crichton.domian.dtos;

import crichton.enumeration.TestResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class TestDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestRequest {
        @Schema(description = "테스트할 소스코드 경로", required = true)
        private String sourcePath;
        @Schema(description = "단위 테스트", required = true)
        private Boolean unitTest;
        @Schema(description = "결함 주입 테스트", required = true)
        private Boolean injectionTest;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestResponse {
        @Schema(description = "단위 테스트 결과", required = true)
        private TestResult unitTestResult;
        @Schema(description = "결함 주입 테스트 결과", required = true)
        private TestResult injectionTestResult;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LogResponse {
        @Schema(description = "로그", required = true)
        private String log;
    }
}
