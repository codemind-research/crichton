package injector.process;

import injector.enumerations.InjectorBinaries;
import injector.setting.DefectInjectorSetting;
import runner.process.ProcessRunner;
import runner.util.CommandBuilder;

public class InjectionTesterRunner extends ProcessRunner {

    private final DefectInjectorSetting setting;

    public InjectionTesterRunner(DefectInjectorSetting setting) {
        super();
        this.setting = setting;
        processBuilder.directory(setting.getProjectWorkspace());
        processBuilder.environment().put("VIPER_PATH", setting.getViperPath());
    }


    @Override
    protected CommandBuilder buildCommand() {
        CommandBuilder command = new CommandBuilder();
        command.addOption("dotnet");
        command.addOption(InjectorBinaries.getFileInResources(InjectorBinaries.INJECTION));
        command.addOption(setting.getSafeSpecFile());
        command.addOption(setting.getOutputFilePath());
        command.addOption(setting.getExeBinaryFilePath());
        return command;
    }

}
