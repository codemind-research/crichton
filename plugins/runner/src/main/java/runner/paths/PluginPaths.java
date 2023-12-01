package runner.paths;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PluginPaths {

    public static final Path USER_PATH = Paths.get(System.getProperty("user.home"));
    public static final Path CRICHTON_PATH = USER_PATH.resolve(".crichton");
    public static final Path CRICHTON_LOG_PATH = CRICHTON_PATH.resolve("crichton.log");
    public static final Path PLUGIN_DIR_PATH = CRICHTON_PATH.resolve("plugins");

    public static Path generatePluginPath(String plugin) {
        return PLUGIN_DIR_PATH.resolve(plugin);
    }

    public static Path generatePluginJarPath(String plugin) {
        return PLUGIN_DIR_PATH.resolve(plugin).resolve(plugin+".jar");
    }


}
