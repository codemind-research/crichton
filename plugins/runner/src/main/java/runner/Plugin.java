package runner;

import lombok.NonNull;
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
    void initialize(@NonNull String targetSource, Map<String,String> pluginSetting) throws IOException;

    /**
     * Executes the plugin.
     *
     * @return true if the plugin execution is successful, false otherwise.
     */
    boolean execute() throws IOException;

    /**
     * Creates a CommandBuilder for plugin-related commands.
     * @return A CommandBuilder instance for building plugin commands.
     */
    default CommandBuilder buildCommand() {
        return new CommandBuilder();
    }

    /**
     * Creates a ProcessBuilder for plugin-related processes.
     *
     * @param command The command to be executed.
     * @return A ProcessBuilder instance for building and executing plugin processes.
     */
    default ProcessBuilder buildProcess(@NonNull List<String> command){
        return new ProcessBuilder();
    }

    /**
     * Transforms plugin-related report data for further processing.
     *
     * @return A ProcessedReportDTO containing the transformed report data.
     * @throws Exception If an error occurs during the transformation process.
     */
    ProcessedReportDTO transformReportData() throws Exception;


}
