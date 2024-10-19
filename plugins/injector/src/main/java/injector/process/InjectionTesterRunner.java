package injector.process;

import injector.setting.DefectInjectorSetting;
import runner.process.DotnetRunner;
import runner.util.CommandBuilder;

import java.util.List;

public class InjectionTesterRunner extends DotnetRunner {

    private final DefectInjectorSetting setting;

    public InjectionTesterRunner(DefectInjectorSetting setting) {
        super();
        this.setting = setting;
        processBuilder.directory(setting.getProjectWorkspace());
        processBuilder.environment().put("VIPER_PATH", setting.getViperPath());
    }


    @Override
    protected CommandBuilder buildCommand() {
        
        var arguments = List.of(
                setting.getSafeSpecFile(),
                setting.getOutputFilePath(),
                setting.getExeBinaryFilePath()
        );
        
        CommandBuilder command = this.buildCommand(setting.getInjectionTesterEngine(), arguments);
        return command;
    }

}
