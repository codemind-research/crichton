package runner;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import runner.dto.ProcessedReportDTO;
import runner.dto.RunResult;
import runner.loader.BasicPluginLoader;
import runner.loader.PluginLoader;
import runner.paths.PluginPaths;
import runner.util.FileUtils;

import java.nio.file.Path;
import java.util.Map;

public class PluginRunner implements Runner  {

    private static final Logger logger = LoggerFactory.getLogger(PluginRunner.class);
    private final String pluginName;
    private final Path pluginJar;
    private final PluginLoader pluginLoader;
    private Plugin plugin;

    public PluginRunner(@NonNull String pluginName) throws Exception {
        this.pluginName = pluginName;
        this.pluginJar = PluginPaths.generatePluginJarPath(pluginName);
        this.pluginLoader = new BasicPluginLoader(pluginJar);
    }

    @Override
    public boolean check(){
        try {
            if (!pluginLoader.isApplicable(pluginJar)) {
                return false;
            }
            plugin = pluginLoader.loadPlugin(pluginName)
                                 .orElseThrow(IllegalArgumentException::new);
            if (plugin.check()){
                return true;
            }else{
                logger.error("Check Failed Plugin: " +pluginName);
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public RunResult run(@NonNull String targetSource, Map<String, String> pluginSetting) {
        try {
            plugin.initialize(targetSource, pluginSetting);
            String start = String.format("\n***************** Start of Plugin : %s ***************** \n", pluginName);
            FileUtils.overWriteDump(PluginPaths.CRICHTON_LOG_PATH.toFile(),start,"\n");
            boolean runResult = plugin.execute();
//            ProcessedReportDTO data = plugin.transformReportData();
            String end = String.format("\n***************** End of Plugin : %s ***************** \n", pluginName);
            FileUtils.overWriteDump(PluginPaths.CRICHTON_LOG_PATH.toFile(),end,"\n");
            return new RunResult(runResult , new ProcessedReportDTO());
        }catch (Exception e) {
            logger.error("Test Failed Plugin Run: " +pluginName);
            return new RunResult(false, new ProcessedReportDTO());
        }
    }
}
