package runner.dto;

import lombok.NonNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

public record PluginOption(UUID id, @NonNull String pluginName, @NonNull String targetSource, Map<String,String> pluginSetting, Path pluginLogPath) {
}
