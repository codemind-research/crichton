package injector.process;

import injector.setting.DefectInjectorSetting;
import runner.process.DotnetProcessRunner;
import runner.util.CommandBuilder;

import java.util.List;
import java.util.Objects;

public class DefectInjectorRunner extends DotnetProcessRunner {

    private final Integer defectSpecId;
    private final DefectInjectorSetting setting;
    private final String targetSource;

    public DefectInjectorRunner(String targetSource, DefectInjectorSetting setting) {
        super();
        this.defectSpecId = null;
        this.targetSource = targetSource;
        this.setting = setting;
    }

    public DefectInjectorRunner(Integer defectSpecId, String targetSource, DefectInjectorSetting setting) {
        super();
        this.defectSpecId = defectSpecId;
        this.targetSource = targetSource;
        this.setting = setting;
    }

    @Override
    protected CommandBuilder buildCommand() {

        var arguments = List.of(
                targetSource,
                setting.getTestSpecFile(),
                getDefectSpecFile(),
                setting.getSafeSpecFile(),
                setting.getTrampoline()
        );

        CommandBuilder command = this.buildCommand(setting.getDefectInjectorEngine(), arguments);

        return command;
    }

    private String getDefectSpecFile() {
        if(Objects.isNull(defectSpecId)) {
            return setting.getDefectSpecFile();
        }
        else {
            return setting.getDefectSpecFile(defectSpecId);
        }
    }

    @Override
    protected ProcessBuilder buildProcess(){
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }
}
