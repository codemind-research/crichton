package injector;

import injector.enumerations.InjectorBinaries;
import injector.enumerations.RunnerStatus;
import injector.process.DefectInjectorRunner;
import injector.process.GoilRunner;
import injector.process.InjectionTesterRunner;
import injector.process.MakePythonRunner;
import injector.report.Parser;
import injector.setting.DefectInjectorSetting;
import runner.Plugin;
import runner.dto.PluginOption;
import runner.dto.ProcessedReportDTO;
import runner.paths.PluginPaths;
import runner.util.FileUtils;

import java.nio.file.Path;
import java.util.*;
import java.util.function.BooleanSupplier;

public class DefectInjectorPlugin implements Plugin {

    private UUID id = UUID.randomUUID();

    private String targetSource;
    private DefectInjectorSetting setting;
    private Path pluginLogPath = PluginPaths.CRICHTON_LOG_PATH;

    @Override
    public boolean check() {
        return InjectorBinaries.allFilesExistInResources();
    }

    @Override
    public void initialize(PluginOption pluginOption) throws Exception {

        if(!Objects.isNull(pluginOption.id())) {
            this.id = pluginOption.id();
        }

        this.targetSource = pluginOption.targetSource();

        if(!Objects.isNull(pluginOption.pluginLogPath())) {
            this.pluginLogPath = pluginOption.pluginLogPath();
        }

        this.setting = new DefectInjectorSetting(pluginOption.pluginName(), pluginOption.pluginSetting());

//        setting.makeDefectJson();
    }

    @Override
    public boolean execute(){
        boolean isPluginSuccess = false;

        // DefectInjector 실행
        if (!runAndContinueOnFailure(RunnerStatus.DEFECT_INJECTOR_RUNNER,
                () -> new DefectInjectorRunner(targetSource, setting).run())) {
            return false;
        }

        if (!runAndContinueOnFailure(RunnerStatus.GOIL_RUNNER,
                () -> new GoilRunner(setting).run())) {
            return false;
        }

        if (!runAndContinueOnFailure(RunnerStatus.MAKE_PYTHON_RUNNER,
                () -> new MakePythonRunner(setting).run())) {
            return false;
        }
        if (!runAndContinueOnFailure(RunnerStatus.INJECTION_TESTER_RUNNER,
                () -> new InjectionTesterRunner(setting).run())) {
            return false;
        }

        return true;
    }

    private boolean runAndContinueOnFailure(RunnerStatus runnerStatus, BooleanSupplier task) {
        boolean result = task.getAsBoolean();
        if (!result) {
            String log = String.format("%s Failed for id: %s \n", runnerStatus.getStatus(), id);
            writeFailedLog(log);
        }
        return result;
    }

    private void writeFailedLog(String failedLog) {
        FileUtils.overWriteDump(pluginLogPath.toFile(),failedLog,"\n");
    }

    @Override
    public ProcessedReportDTO transformReportData()  {
        Parser parser = new Parser(setting);
        return ProcessedReportDTO.builder()
                .pluginName(setting.getPluginName())
                .info(parser.convert())
                .build();
    }


}
