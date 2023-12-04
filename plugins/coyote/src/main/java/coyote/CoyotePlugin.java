package coyote;

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
    private String projectSetting;


    @Override
    public boolean check() {
        String symbolicLink = "/usr/bin/coyoteCli";
        Path path = FileSystems.getDefault().getPath(symbolicLink);
        return !Files.isSymbolicLink(path) || targetSource.isBlank();
    }

    @Override
    public void initialize(@NonNull String targetSource, Map<String, String> coyoteSetting) {
        this.targetSource = targetSource;
        CoyoteSetting setting = new CoyoteSetting(coyoteSetting);
        this.reportFile = new File(setting.getReport());
        this.projectSetting = setting.getProjectSetting();
    }

    @Override
    public boolean execute() {
        try {
            Process process = buildProcess(buildCommand().getCommand()).start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public ProcessBuilder buildProcess(@NonNull List<String> command) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(command);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(OUTPUT_PATH.toFile()));
        return processBuilder;
    }

    @Override
    public ProcessedReportDTO transformReportData() throws Exception {
        if (!reportFile.exists())
            return new ProcessedReportDTO();
        StringBuilder csvData = FileUtils.readFile(reportFile);
        return CsvParser.parser(csvData.toString());
    }

    @Override
    public CommandBuilder buildCommand() {
        CommandBuilder command = new CommandBuilder();
        command.addOption("coyoteCli");
        command.addOption("-n", targetSource);
        command.addOption("-o", reportFile.getAbsolutePath());
        command.checkAndAddOption("-p", projectSetting, () -> new File(projectSetting).exists());
        return command;
    }


}
