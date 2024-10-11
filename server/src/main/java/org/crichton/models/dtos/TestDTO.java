package org.crichton.models.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import runner.dto.ProcessedReportDTO;

import java.util.HashMap;
import java.util.List;

public class TestDTO {


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PluginRequest {
        @NotNull
        @Schema(description = "Plugin")
        private String plugin;
        @NotNull
        @Schema(description = "Path to Test Source Code")
        private String sourcePath;
        @Schema(description = "Plugin Configuration Information")
        private HashMap<String,String> pluginSettings;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestResponse {
        @NotNull
        @Schema(description = "Test Result")
        private Boolean testResult;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PluginResponse {
        @Schema(description = "Plugin List")
        private List<PluginSetting> pluginList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PluginSetting {
        @Schema(description = "Plugin")
        private String plugin;
        @Schema(description = "Setting")
        private HashMap<String,String> setting;
    }
}
