package injector.process;

import injector.setting.DefectInjectorSetting;
import runner.paths.PluginPaths;
import runner.process.ProcessRunner;
import runner.util.CommandBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;

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
        CommandBuilder command = new CommandBuilder();
        command.addOption(setting.getGoilProcess());
        command.addOption("--target=posix/linux");
        command.addOption("--templates="+setting.getGoilTemplates());
        command.addOption(setting.getOilFile().getAbsolutePath());
        return command;
    }

    @Override
    protected ProcessBuilder buildProcess(){
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }
}
