package injector;

import injector.enumerations.InjectorBinaries;
import injector.enumerations.RunnerStatus;
import injector.process.DefectInjectorRunner;
import injector.process.GoilRunner;
import injector.process.InjectionTesterRunner;
import injector.process.MakePythonRunner;
import injector.report.Parser;
import injector.setting.DefectInjectorSetting;
import lombok.NonNull;
import runner.Plugin;
import runner.dto.ProcessedReportDTO;
import runner.paths.PluginPaths;
import runner.util.FileUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class DefectInjectorPlugin implements Plugin {

    private String targetSource;
    private DefectInjectorSetting setting;

    @Override
    public boolean check() {
        return InjectorBinaries.allFilesExistInResources();
    }

    @Override
    public void initialize(@NonNull String pluginName, @NonNull String targetSource, Map<String, String> pluginSetting) throws Exception {
        this.targetSource = targetSource;
        this.setting = new DefectInjectorSetting(pluginName, pluginSetting);
        setting.makeDefectJson();
    }

    @Override
    public boolean execute(){
        boolean isPluginSuccess = false;
        for (int i = 1; i < setting.getDefectLength(); i++) {
            int id = i;
            if (!runAndContinueOnFailure(RunnerStatus.DEFECT_INJECTOR_RUNNER, id ,
                    () -> new DefectInjectorRunner(targetSource, setting, id).run())) {
                continue;
            }
            setting.moveCRCFile(targetSource, id);
            if (!runAndContinueOnFailure(RunnerStatus.GOIL_RUNNER, id ,
                    () -> new GoilRunner(setting).run())) {
                continue;
            }
            if (!runAndContinueOnFailure(RunnerStatus.MAKE_PYTHON_RUNNER, id ,
                    () -> new MakePythonRunner(setting).run())) {
                continue;
            }
            if (!runAndContinueOnFailure(RunnerStatus.INJECTION_TESTER_RUNNER, id ,
                    () -> new InjectionTesterRunner(setting, id).run())) {
                continue;
            }
            isPluginSuccess = true;
        }
        return isPluginSuccess;
    }

    private boolean runAndContinueOnFailure(RunnerStatus runnerStatus, int id , BooleanSupplier task) {
        boolean result = task.getAsBoolean();
        if (!result) {
            String log = String.format("%s Failed for id: %d \n", runnerStatus.getStatus(), id);
            writeFailedLog(log);
        }
        return result;
    }

    private void writeFailedLog(String failedLog) {
        FileUtils.overWriteDump(PluginPaths.CRICHTON_LOG_PATH.toFile(),failedLog,"\n");
    }

    @Override
    public ProcessedReportDTO transformReportData()  {
        Parser parser = new Parser(setting);;
        return ProcessedReportDTO.builder()
                .pluginName(setting.getPluginName())
                .info(parser.convert())
                .build();
    }


}
