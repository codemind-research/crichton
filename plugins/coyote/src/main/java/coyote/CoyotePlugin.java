package coyote;

import coyote.process.CoyoteRunner;
import coyote.report.CsvParser;
import coyote.setting.CoyoteSetting;
import lombok.NonNull;
import runner.Plugin;
import runner.dto.ProcessedReportDTO;
import runner.dto.RunResult;
import runner.util.CommandBuilder;
import runner.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class CoyotePlugin implements Plugin {

    private String targetSource;
    private File reportFile;
    private File projectSetting;


    @Override
    public boolean check() {
        String symbolicLink = "/usr/bin/coyoteCli";
        Path path = FileSystems.getDefault().getPath(symbolicLink);
        return Files.isSymbolicLink(path);
    }

    @Override
    public void initialize(@NonNull String pluginName, @NonNull String targetSource, Map<String, String> coyoteSetting) {
        this.targetSource = targetSource;
        CoyoteSetting setting = new CoyoteSetting(coyoteSetting);
        this.reportFile = new File(setting.getReport());
        this.projectSetting =  new File(setting.getProjectSetting());
    }

    @Override
    public boolean execute() {
        return new CoyoteRunner(targetSource,reportFile,projectSetting).run();
    }

    @Override
    public ProcessedReportDTO transformReportData() {
        try {
            if (!reportFile.exists())
                return new ProcessedReportDTO();
            StringBuilder csvData = FileUtils.readFile(reportFile);
            return CsvParser.parser(csvData.toString());
        }catch (Exception e) {
            return ProcessedReportDTO.builder()
                                     .build();
        }
    }



}
