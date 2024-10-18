package injector.process;

import injector.setting.DefectInjectorSetting;
import runner.process.ProcessRunner;
import runner.util.CommandBuilder;

public class MakePythonRunner extends ProcessRunner {

    private final DefectInjectorSetting setting;

    public MakePythonRunner(DefectInjectorSetting setting) {
        super();
        this.setting = setting;
        processBuilder.directory(setting.getProjectWorkspace());
    }

    @Override
    protected CommandBuilder buildCommand() {
        CommandBuilder command = new CommandBuilder();
        command.addOption("./make.py");
        return command;
    }


    @Override
    protected ProcessBuilder buildProcess(){
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }
}
