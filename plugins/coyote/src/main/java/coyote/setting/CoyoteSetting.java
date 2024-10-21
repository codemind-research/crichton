package coyote.setting;

import coyote.util.CoyotePluginProperties;
import runner.paths.PluginPaths;
import runner.setting.PluginSetting;
import runner.util.constants.PluginConfigurationKey;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

public class CoyoteSetting extends PluginSetting {

    private String report;
    private final String projectSettingFileName;

    private final File unitTestDir;
    private final CoyotePluginProperties properties;

    public CoyoteSetting(String pluginName, Map<String, String> configuration) {
        super(pluginName, configuration);

        this.unitTestDir = Paths.get(this.workingDirectory.getAbsolutePath(), configuration.getOrDefault(PluginConfigurationKey.UnitTester.DIR_NAME, "coyote_unit_test")).toFile();
        this.projectSettingFileName = configuration.getOrDefault(PluginConfigurationKey.UnitTester.UNIT_TEST_PROJECT_SETTING_FILE_NAME,"");

        var propertiesDir = configuration.getOrDefault(PluginConfigurationKey.PROPERTIES_PATH, PluginPaths.generatePluginSettingsPath(pluginName).toString());
        this.properties = CoyotePluginProperties.loadProperties(propertiesDir);
    }

    public String getEnginePath() {
        return this.properties.getEnginePath();
    }

    public String getReport() {
        return this.unitTestDir.toPath().resolve(report).toFile().getAbsolutePath();
    }

    public String getProjectSetting() {
        return this.unitTestDir.toPath().resolve(projectSettingFileName).toFile().getAbsolutePath();
    }
}
