package crichton.paths;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryPaths {

    private static final String CRICHTON_LOG_FILE = "crichton.log";
    private static final String PLUGIN_PROPERTY_FILE = "plugin.properties";
    public static final Path USER_PATH = Paths.get(System.getProperty("user.home"));
    public static final Path CRICHTON_PATH = USER_PATH.resolve(".crichton");
    public static final Path CRICHTON_LOG_PATH = CRICHTON_PATH.resolve(CRICHTON_LOG_FILE);
    public static final Path PLUGIN_PATH = CRICHTON_PATH.resolve("plugins");
    public static final Path UPLOAD_PATH = CRICHTON_PATH.resolve("source");

    static {
        File crichtonDir = CRICHTON_PATH.toFile();
        if (!crichtonDir.exists())
            crichtonDir.mkdir();
    }

    public static Path generateUnzipPath(String sourceName) {
        return UPLOAD_PATH.resolve(sourceName);
    }

    public static Path generateZipPath(String zipPath) {
        return UPLOAD_PATH.resolve(zipPath);
    }

    public static Path generatePluginsPath(String pluginName) {
        return PLUGIN_PATH.resolve(pluginName);
    }

    public static Path generatePluginZipPath(String pluginName, String zipPath) {
        return generatePluginsPath(pluginName).resolve(zipPath);
    }

    public static Path generatePluginUnZipPath(String pluginName, String settings) {
        return generatePluginsPath(pluginName).resolve(settings);
    }

    public static Path generatePluginPropertiesPath(String pluginName) {
        return PLUGIN_PATH.resolve(pluginName).resolve(PLUGIN_PROPERTY_FILE);
    }

}
