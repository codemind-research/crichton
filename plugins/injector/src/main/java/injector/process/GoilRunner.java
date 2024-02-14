package injector.process;

import injector.setting.DefectInjectorSetting;
import runner.paths.PluginPaths;
import runner.process.ProcessRunner;
import runner.util.CommandBuilder;

public class GoilRunner extends ProcessRunner {

    private final DefectInjectorSetting setting;

    public GoilRunner(DefectInjectorSetting setting) {
        super();
        this.setting = setting;
        processBuilder.directory(PluginPaths.generatePluginSettingsPath(setting.getPluginName()).toFile());
    }

    @Override
    protected CommandBuilder buildCommand() {
        CommandBuilder command = new CommandBuilder();
        command.addOption("goil");
        command.addOption("--target=posix/linux");
        command.addOption("--templates="+setting.getGoilTemplates());
        command.addOption(setting.getOilCrOilFile().getAbsolutePath());
        return command;
    }

    @Override
    protected ProcessBuilder buildProcess(){
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }
}
