package injector.process;

import injector.setting.DefectInjectorSetting;
import runner.paths.PluginPaths;
import runner.process.ProcessRunner;
import runner.util.CommandBuilder;

import java.util.List;

public class GoilRunner extends ProcessRunner {

    private final DefectInjectorSetting setting;

    public GoilRunner(DefectInjectorSetting setting) {
        super();
        this.setting = setting;
        var workingDir = PluginPaths.generatePluginSettingsPath(setting.getPluginName()).toFile();
        if(!workingDir.exists() && setting.getProjectWorkspace().exists()) {
            workingDir = setting.getProjectWorkspace();
        }
        processBuilder.directory(workingDir);
        processBuilder.environment().put("PATH", System.getenv("PATH"));
    }

    @Override
    protected CommandBuilder buildCommand() {

        var arguments = List.of(
                "--target=posix/linux",
                String.format("--templates=%s", setting.getGoilTemplates()),
                setting.getDefectSimulationOilFileName().getAbsolutePath()
        );

        return buildCommand(arguments);
    }

    @Override
    protected ProcessBuilder buildProcess(){
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }

    @Override
    protected String getProcessName() {
        return setting.getGoilProcess();
    }
}
