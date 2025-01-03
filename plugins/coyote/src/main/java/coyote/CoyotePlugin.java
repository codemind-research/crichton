package coyote;

import coyote.process.CoyoteRunner;
import coyote.report.CsvParser;
import coyote.setting.CoyoteSetting;
import runner.Plugin;
import runner.dto.PluginOption;
import runner.dto.ProcessedReportDTO;
import runner.paths.PluginPaths;
import runner.util.FileUtils;

import java.io.File;
import java.nio.file.*;

public class CoyotePlugin implements Plugin {

    private String targetSource;
    private CoyoteSetting setting;
    private Path pluginLogPath = PluginPaths.CRICHTON_LOG_PATH;


    @Override
    public boolean check() {

        if(setting != null) {
            String enginePath = setting.getEnginePath();
            Path path = FileSystems.getDefault().getPath(enginePath);
            return Files.isSymbolicLink(path) || Files.isRegularFile(path);
        }
        else {
            String symbolicLink = "/usr/bin/CoyoteCLI";
            Path path = FileSystems.getDefault().getPath(symbolicLink);
            return Files.isSymbolicLink(path);
        }
    }

    @Override
    public void initialize(PluginOption pluginOption) {
        this.targetSource = pluginOption.targetSource();
        this.setting = new CoyoteSetting(pluginOption.pluginName(), pluginOption.pluginSetting());
    }

    @Override
    public boolean execute() {
        if(check()) {
            return new CoyoteRunner(targetSource, this.setting).run();
        } else {
            throw new RuntimeException("Coyote dose not exists.");
        }
    }

    @Override
    public ProcessedReportDTO transformReportData() {
        try {

            var reportFile = new File(this.setting.getReportFilePath());

            if (!reportFile.exists()) {
                throw new NoSuchFileException(String.format("Report file does not exist: %s", reportFile.getAbsolutePath()));
            }
            StringBuilder csvData = FileUtils.readFile(reportFile);
            return CsvParser.parser(csvData.toString());
        }catch (Exception e) {
            FileUtils.overWriteDump(pluginLogPath.toFile(), e.getMessage(),"\n");
            return ProcessedReportDTO.builder()
                                     .build();
        }
    }

    @Override
    public void setLogFilePath(Path logFilePath) {
        this.pluginLogPath = logFilePath;
    }
}
