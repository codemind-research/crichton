package runner;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import runner.dto.ProcessedReportDTO;
import runner.dto.RunResult;
import runner.loader.BasicPluginLoader;
import runner.loader.PluginLoader;
import runner.paths.PluginPaths;

import java.nio.file.Path;
import java.util.Map;

public class PluginRunner implements Runner  {

    private static final Logger logger = LoggerFactory.getLogger(PluginRunner.class);
    private final String pluginName;
    private final Path pluginJar;
    private final PluginLoader pluginLoader;
    private final String targetSource;
    private final Map<String, String> pluginSetting;

    public PluginRunner(@NonNull String pluginName, @NonNull String targetSource, Map<String, String> pluginSetting) throws Exception {
        this.pluginName = pluginName;
        this.targetSource = targetSource;
        this.pluginJar = PluginPaths.generatePluginJarPath(pluginName);
        this.pluginLoader = new BasicPluginLoader(pluginJar);
        this.pluginSetting = pluginSetting;
    }

    @Override
    public RunResult run() {
        try {
            if (!pluginLoader.isApplicable(pluginJar)) {
                return new RunResult(false, new ProcessedReportDTO());
            }
            Plugin plugin = pluginLoader.loadPlugin(pluginName)
                                        .orElseThrow(IllegalArgumentException::new);
            plugin.initialize(targetSource, pluginSetting);
            if (plugin.check()){
                logger.error("Check Failed Plugin: " +pluginName);
                return new RunResult(false, new ProcessedReportDTO());
            }
            boolean runResult = plugin.execute();
//            ProcessedReportDTO data = plugin.transformReportData();
            return new RunResult(runResult , new ProcessedReportDTO());
        }catch (Exception e) {
            logger.error("Test Failed Plugin Run: " +pluginName);
            return new RunResult(false, new ProcessedReportDTO());
        }
    }
}
