package runner;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import runner.dto.PluginOption;
import runner.dto.ProcessedReportDTO;
import runner.dto.RunResult;
import runner.loader.BasicPluginLoader;
import runner.loader.PluginLoader;
import runner.paths.PluginPaths;
import runner.util.FileUtils;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PluginRunner implements Runner  {

    private static final Logger logger = LoggerFactory.getLogger(PluginRunner.class);
    private final String pluginName;
    private final Path pluginDirectory;
    private final Path pluginLogFile;
    private final Path pluginJar;
    private final PluginLoader pluginLoader;
    private Plugin plugin;

    public PluginRunner(@NonNull String pluginName) throws Exception {
        this.pluginName = pluginName;
        this.pluginDirectory = PluginPaths.PLUGIN_DIR_PATH;
        this.pluginLogFile = PluginPaths.generatePluginLogPath(PluginPaths.PLUGIN_DIR_PATH.resolve(pluginName));
        this.pluginJar = PluginPaths.generatePluginJarPath(pluginName);
        this.pluginLoader = new BasicPluginLoader(pluginJar);
        validate();
    }

    public PluginRunner(@NonNull String pluginDirectory, @NonNull String pluginName) throws Exception {
        this.pluginName = pluginName.replace(".jar", "");
        this.pluginDirectory = Paths.get(pluginDirectory);

        // 현재 날짜를 yyyy-MM-dd 형식으로 가져오기
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String logFileName = currentDate.format(formatter) + ".log";
        this.pluginLogFile = PluginPaths.generatePluginLogPath(this.pluginDirectory.resolve("log"), logFileName);

        this.pluginJar = PluginPaths.generatePluginJarPath(pluginDirectory, pluginName);

        this.pluginLoader = new BasicPluginLoader(pluginJar);

        validate();
    }

    private void validate() throws NoSuchFileException {

        if(!this.pluginDirectory.toFile().exists() || !this.pluginDirectory.toFile().isDirectory()) {
            throw new NoSuchFileException(String.format("Plugin directory does not exist or is not a directory: %s", this.pluginDirectory));
        }

        if(!this.pluginJar.toFile().exists()) {
            throw new NoSuchFileException(String.format("Plugin jar does not exist: %s", this.pluginJar));
        }
    }

    @Override
    public boolean check() {
        try {
            if (!pluginLoader.isApplicable(pluginJar)) {
                var message = String.format("Plugin '%s' not found in directory '%s'", pluginJar.getFileName(), pluginJar.getParent());
                throw new NoSuchFileException(message);
            }

            plugin = pluginLoader.loadPlugin(pluginName)
                                 .orElseThrow(IllegalArgumentException::new);

            if (plugin.check()) {
                return true;
            }
            else {
                throw new IllegalStateException("Plugin check returned false.");
            }

        }
        catch (IllegalArgumentException e) {
            throw e;
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public RunResult run(@NonNull String targetSource, Map<String, String> pluginSetting) {
        try {

            UUID projectId = null;
            if(pluginSetting.containsKey("project_id")) {
                projectId = UUID.fromString(pluginSetting.get("project_id"));
            }

            var pluginOption = new PluginOption(projectId, pluginName, targetSource, pluginSetting, pluginLogFile);
            plugin.initialize(pluginOption);
            plugin.setLogFilePath(this.pluginLogFile);

            String start = String.format("\nStart of Plugin : %s  \n", pluginName);
            FileUtils.overWriteDump(pluginLogFile.toFile(), start,"\n");

            boolean runResult = plugin.execute();

            ProcessedReportDTO data = plugin.transformReportData();

            String end = String.format("\nPlugin completed : %s  \n", pluginName);
            FileUtils.overWriteDump(pluginLogFile.toFile(),end,"\n");

            return new RunResult(runResult , data);
        }catch (Exception e) {
            logger.error("Test Failed Plugin Run: {}", pluginName);
            return new RunResult(false, new ProcessedReportDTO());
        }
    }
}
