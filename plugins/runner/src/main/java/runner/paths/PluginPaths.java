package runner.paths;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PluginPaths {

    private static final String CRICHTON_LOG_FILE = "crichton.log";
    private static final String PLUGIN_PROPERTY_FILE = "plugin.properties";
    public static final Path USER_PATH = Paths.get(System.getProperty("user.home"));
    public static final Path CRICHTON_PATH = USER_PATH.resolve(".crichton");
    public static final Path CRICHTON_LOG_PATH = CRICHTON_PATH.resolve(CRICHTON_LOG_FILE);
    public static final Path PLUGIN_DIR_PATH = CRICHTON_PATH.resolve("plugins");

    public static Path generatePluginPath(String plugin) {
        return PLUGIN_DIR_PATH.resolve(plugin);
    }

    public static Path generatePluginJarPath(String plugin) {
        return PLUGIN_DIR_PATH.resolve(plugin).resolve(plugin+".jar");
    }

    public static Path generatePluginsPath(String pluginName) {
        return PLUGIN_DIR_PATH.resolve(pluginName);
    }

    public static Path generatePluginZipPath(String pluginName, String zipPath) {
        return generatePluginsPath(pluginName).resolve(zipPath);
    }

    public static Path generatePluginUnZipPath(String pluginName, String settings) {
        return generatePluginsPath(pluginName).resolve(settings);
    }

    public static Path generatePluginPropertiesPath(String pluginName) {
        return PLUGIN_DIR_PATH.resolve(pluginName).resolve(PLUGIN_PROPERTY_FILE);
    }

    public static Path generatePluginSettingsPath(String pluginName) {
        return PLUGIN_DIR_PATH.resolve(pluginName).resolve("setting");
    }

}
