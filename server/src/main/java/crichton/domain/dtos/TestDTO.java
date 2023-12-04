package crichton.domain.dtos;

import crichton.enumeration.TestResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.HashMap;
import java.util.List;

public class TestDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnitTestRequest {
        @Schema(description = "테스트할 소스코드 경로", required = true)
        private String sourcePath;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InjectionTestRequest {
        @Schema(description = "결함 주입 테스트에 들어가는 지속시간", required = true)
        private int testDuration;
    }


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestResponse {
        @Schema(description = "테스트 결과", required = true)
        private TestResult testResult;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProgressResponse {
        @Schema(description = "테스트 진행 상태", required = true)
        private String progress;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PluginResponse {
        @Schema(description = "플러그인 리스트")
        private List<PluginSetting> pluginList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PluginSetting {
        @Schema(description = "플러그인")
        private String plugin;
        @Schema(description = "설정")
        private HashMap<String,String> setting;
    }
}
