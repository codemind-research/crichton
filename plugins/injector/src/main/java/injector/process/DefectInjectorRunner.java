package injector.process;

import injector.enumerations.InjectorBinaries;
import injector.setting.DefectInjectorSetting;
import runner.process.ProcessRunner;
import runner.util.CommandBuilder;

import java.io.File;

public class DefectInjectorRunner extends ProcessRunner {

    private final DefectInjectorSetting setting;
    private final String targetSource;
    private final int id;

    public DefectInjectorRunner(String targetSource, DefectInjectorSetting setting, int id) {
        super();
        this.targetSource = targetSource;
        this.setting = setting;
        this.id = id;
    }

    @Override
    protected CommandBuilder buildCommand() {
        CommandBuilder command = new CommandBuilder();
        command.addOption("dotnet");
        command.addOption(InjectorBinaries.getFileInResources(InjectorBinaries.DEFECT));
        command.addOption(targetSource + File.separator + setting.getTarget(id));
        command.addOption(setting.getOilFile().getAbsolutePath());
        command.addOption(setting.getDefectNumberJson(id));
        command.addOption(setting.getSafeJson().getAbsolutePath());
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
