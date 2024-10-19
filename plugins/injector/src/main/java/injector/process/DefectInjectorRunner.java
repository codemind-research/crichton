package injector.process;

import injector.setting.DefectInjectorSetting;
import runner.process.DotnetRunner;
import runner.util.CommandBuilder;

import java.util.List;

public class DefectInjectorRunner extends DotnetRunner {

    private final DefectInjectorSetting setting;
    private final String targetSource;

    public DefectInjectorRunner(String targetSource, DefectInjectorSetting setting) {
        super();
        this.targetSource = targetSource;
        this.setting = setting;
    }

    @Override
    protected CommandBuilder buildCommand() {

        var arguments = List.of(
                targetSource,
                setting.getTestSpecFile(),
                setting.getDefectSpecFile(),
                setting.getSafeSpecFile(),
                setting.getTrampoline()
        );

        CommandBuilder command = this.buildCommand(setting.getInjectionTesterEngine(), arguments);

        return command;
    }

    @Override
    protected ProcessBuilder buildProcess(){
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }
}
