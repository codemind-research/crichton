package injector.process;

import injector.enumerations.InjectorBinaries;
import injector.setting.DefectInjectorSetting;
import runner.process.ProcessRunner;
import runner.util.CommandBuilder;

public class InjectionTesterRunner extends ProcessRunner {

    private final DefectInjectorSetting setting;
    private final int id;

    public InjectionTesterRunner(DefectInjectorSetting setting, int id) {
        super();
        this.setting = setting;
        this.id = id;
        processBuilder.directory(setting.getPluginSettingDir());
        processBuilder.environment().put("VIPER_PATH",setting.getViperPath());
    }


    @Override
    protected CommandBuilder buildCommand() {
        CommandBuilder command = new CommandBuilder();
        command.addOption("dotnet");
        command.addOption(InjectorBinaries.getFileInResources(InjectorBinaries.INJECTION));
        command.addOption(setting.getSafeSpecFile().getAbsolutePath());
        command.addOption(setting.getOutputName(id));
        command.addOption(setting.getExeBinary(id));
        return command;
    }

}
