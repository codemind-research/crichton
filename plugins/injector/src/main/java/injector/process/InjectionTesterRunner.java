package injector.process;

import injector.setting.DefectInjectorSetting;
import runner.process.DotnetProcessRunner;
import runner.util.CommandBuilder;

import java.util.List;
import java.util.Objects;

public class InjectionTesterRunner extends DotnetProcessRunner {


    private final Integer defectSpecId;
    private final DefectInjectorSetting setting;

    public InjectionTesterRunner(DefectInjectorSetting setting) {
        this(null, setting);
    }

    public InjectionTesterRunner(Integer defectSpecId, DefectInjectorSetting setting) {
        super();
        this.defectSpecId = defectSpecId;
        this.setting = setting;
        processBuilder.directory(setting.getSourceDirectory());
        processBuilder.environment().put("VIPER_PATH", setting.getViperPath());
    }


    @Override
    protected CommandBuilder buildCommand() {
        
        var arguments = List.of(
                setting.getSafeSpecFile(),
                getOutputFilePath(),
                setting.getExeBinaryFilePath()
        );
        
        CommandBuilder command = this.buildCommand(setting.getInjectionTesterEngine(), arguments);
        return command;
    }

    private String getOutputFilePath() {
        if(Objects.isNull(defectSpecId)) {
            return setting.getOutputFilePath();
        }
        else {
            return setting.getOutputFilePath(defectSpecId);
        }
    }

}
