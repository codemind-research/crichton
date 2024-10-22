package runner.setting;

import runner.paths.PluginPaths;
import runner.util.constants.PluginConfigurationKey;

import java.io.File;
import java.util.Map;

public class PluginSetting {


    protected final String pluginName;
    protected final File workingDirectory;

    public PluginSetting(String pluginName, Map<String, String> configuration) {
        this.pluginName = pluginName;
        var workspace = configuration.getOrDefault(PluginConfigurationKey.WORKSPACE, PluginPaths.generatePluginSettingsPath(pluginName).toString());
        this.workingDirectory = new File(workspace);
    }

}
