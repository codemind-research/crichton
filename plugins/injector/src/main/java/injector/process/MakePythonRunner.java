package injector.process;

import injector.setting.DefectInjectorSetting;
import runner.process.ProcessRunner;
import runner.process.PythonProcessRunner;
import runner.util.CommandBuilder;

import java.util.List;

public class MakePythonRunner extends PythonProcessRunner {

    private final DefectInjectorSetting setting;

    public MakePythonRunner(DefectInjectorSetting setting) {
        super();
        this.setting = setting;
        processBuilder.directory(setting.getProjectWorkspace());
    }

    @Override
    protected CommandBuilder buildCommand() {
        return buildCommand(List.of(setting.getMakeFilePath("make.py")));
    }


    @Override
    protected ProcessBuilder buildProcess(){
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }
}
