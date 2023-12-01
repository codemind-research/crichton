package runner.loader;

import lombok.NonNull;
import runner.Plugin;

import java.nio.file.Path;
import java.util.Optional;

public interface PluginLoader {

    boolean isApplicable(Path pluginPath);

    Optional<Plugin> loadPlugin(@NonNull String pluginName) throws Exception;
}
