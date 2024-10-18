package injector.process;

import injector.enumerations.InjectorBinaries;
import injector.setting.DefectInjectorSetting;
import runner.process.ProcessRunner;
import runner.util.CommandBuilder;

public class DefectInjectorRunner extends ProcessRunner {

    private final DefectInjectorSetting setting;
    private final String targetSource;

    public DefectInjectorRunner(String targetSource, DefectInjectorSetting setting) {
        super();
        this.targetSource = targetSource;
        this.setting = setting;
    }

    @Override
    protected CommandBuilder buildCommand() {
        CommandBuilder command = new CommandBuilder();
        command.addOption("dotnet");
        command.addOption(InjectorBinaries.getFileInResources(InjectorBinaries.DEFECT));
        command.addOption(targetSource);
        command.addOption(setting.getTestSpecFile().getAbsolutePath());
        command.addOption(setting.getDefectSpecFile().getAbsolutePath());
        command.addOption(setting.getSafeSpecFile().getAbsolutePath());
        command.addOption(setting.getTrampoline());
        return command;
    }

    @Override
    protected ProcessBuilder buildProcess(){
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }
}
