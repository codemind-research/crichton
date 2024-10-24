package runner;

import lombok.NonNull;
import runner.dto.PluginOption;
import runner.dto.ProcessedReportDTO;
import runner.dto.RunResult;
import runner.paths.PluginPaths;
import runner.util.CommandBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface Plugin {

    // Path for storing test logs
    Path OUTPUT_PATH = PluginPaths.CRICHTON_LOG_PATH;

    /**
     * Checks the preconditions before executing the plugin.
     *
     * @return true if the preconditions are met, false otherwise.
     */
    boolean check();

    /**
     * Initializes the plugin settings.
     * @param targetSource   The target source for the plugin.
     * @param pluginSetting  A map containing plugin-specific settings.
     * @throws IOException   If an I/O error occurs during initialization.
     */
    void initialize(PluginOption pluginOption) throws Exception;

    /**
     * Executes the plugin.
     *
     * @return true if the plugin execution is successful, false otherwise.
     */
    boolean execute() throws Exception;

    /**
     * Transforms plugin-related report data for further processing.
     *
     * @return A ProcessedReportDTO containing the transformed report data.
     * @throws Exception If an error occurs during the transformation process.
     */
    ProcessedReportDTO transformReportData();

    void setLogFilePath(Path logFilePath);
}
