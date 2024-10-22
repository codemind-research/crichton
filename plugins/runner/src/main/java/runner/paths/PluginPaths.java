package runner.paths;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PluginPaths {

    private static final String CRICHTON_LOG_FILE = "crichton.log";
    public static final String PLUGIN_PROPERTY_FILE = "plugin.properties";
    public static final Path USER_PATH = Paths.get(System.getProperty("user.home"));
    public static final Path CRICHTON_PATH = USER_PATH.resolve(".crichton");
    public static final Path CRICHTON_LOG_PATH = CRICHTON_PATH.resolve(CRICHTON_LOG_FILE);
    public static final Path PLUGIN_DIR_PATH = CRICHTON_PATH.resolve("plugins");

    public static Path generatePluginPath(String plugin) {
        return PLUGIN_DIR_PATH.resolve(plugin);
    }

    public static Path generatePluginJarPath(String directory, String pluginFileName) {
        return generatePluginJarPath(Paths.get(directory), pluginFileName);
    }

    public static Path generatePluginJarPath(String pluginName) {
        var pluginDirPath = PLUGIN_DIR_PATH.resolve(pluginName);
        return generatePluginJarPath(pluginDirPath, pluginName + ".jar");
    }

    public static Path generatePluginJarPath(Path pluginDirectory, String pluginFileName) {
        return pluginDirectory.resolve(pluginFileName);
    }

    public static Path generatePluginsPath(String pluginName) {
        return PLUGIN_DIR_PATH.resolve(pluginName);
    }

    public static Path generatePluginZipPath(String pluginName, String zipPath) {
        return generatePluginsPath(pluginName).resolve(zipPath);
    }

    public static Path generatePluginLogPath(Path pluginLogDirectory) {
        return pluginLogDirectory.resolve(CRICHTON_LOG_PATH);
    }

    public static Path generatePluginUnZipPath(String pluginName, String settings) {
        return generatePluginsPath(pluginName).resolve(settings);
    }

    public static Path generatePluginPropertiesPath(String pluginPropertiesDirectoryPath, String pluginName, String fileName) {
        return generatePluginPropertiesPath(Paths.get(pluginPropertiesDirectoryPath, pluginName), fileName);
    }

    public static Path generatePluginPropertiesPath(String pluginName) {
        return generatePluginPropertiesPath(PLUGIN_DIR_PATH.resolve(pluginName), PLUGIN_PROPERTY_FILE);
    }

    public static Path generatePluginPropertiesPath(String pluginPropertiesPath, String fileName) {
        return generatePluginPropertiesPath(Paths.get(pluginPropertiesPath), fileName);
    }

    public static Path generatePluginPropertiesPath(Path pluginPropertiesPath, String fileName) {
        return pluginPropertiesPath.resolve(fileName).normalize().toAbsolutePath();
    }



    public static Path generatePluginSettingsPath(String pluginName) {
        return PLUGIN_DIR_PATH.resolve(pluginName).resolve("setting");
    }

}
