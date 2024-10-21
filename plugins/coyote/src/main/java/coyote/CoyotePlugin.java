package coyote;

import coyote.process.CoyoteRunner;
import coyote.report.CsvParser;
import coyote.setting.CoyoteSetting;
import lombok.NonNull;
import runner.Plugin;
import runner.dto.PluginOption;
import runner.dto.ProcessedReportDTO;
import runner.dto.RunResult;
import runner.util.CommandBuilder;
import runner.util.FileUtils;
import runner.util.constants.FileName;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class CoyotePlugin implements Plugin {

    private String targetSource;
    private CoyoteSetting setting;


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

            var reportFile = new File(this.setting.getReport());

            if (!reportFile.exists()) {
                throw new NoSuchFileException(String.format("Report file does not exist: %s", reportFile.getAbsolutePath()));
            }
            StringBuilder csvData = FileUtils.readFile(reportFile);
            return CsvParser.parser(csvData.toString());
        }catch (Exception e) {
            return ProcessedReportDTO.builder()
                                     .build();
        }
    }



}
