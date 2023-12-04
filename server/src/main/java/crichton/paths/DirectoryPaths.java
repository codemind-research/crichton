package crichton.paths;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryPaths {

    private static final String PLUGIN_PROPERTY_FILE = "plugin.properties";
    public static final Path USER_PATH = Paths.get(System.getProperty("user.home"));
    public static final Path CLI_PATH = USER_PATH.resolve("coyoteCli");
    public static final Path CRICHTON_PATH = USER_PATH.resolve(".crichton");
    public static final Path PLUGIN_PATH = CRICHTON_PATH.resolve("plugins");
    public static final Path UPLOAD_PATH = CLI_PATH.resolve("source");
    public static final Path REPORT_PATH = CLI_PATH.resolve("report");

    public static Path generateUnzipPath(String sourceName) {
        return UPLOAD_PATH.resolve(sourceName);
    }

    public static Path generateZipPath(String zipPath) {
        return UPLOAD_PATH.resolve(zipPath);
    }

    public static Path generateUnitReportFilePath(String sourceName) {
        return REPORT_PATH.resolve(sourceName+"_unitTest.csv");
    }

    public static Path generateInjectionReportFilePath(String sourceName) {
        return REPORT_PATH.resolve(sourceName+"_injectionTest.csv");
    }

    public static Path generateSettingsPath(String sourceName) {
        return CLI_PATH.resolve(sourceName+"_projectSettings.json");
    }

    public static Path generatePluginPropertiesPath(String pluginName) {
        return PLUGIN_PATH.resolve(pluginName).resolve(PLUGIN_PROPERTY_FILE);
    }

}
