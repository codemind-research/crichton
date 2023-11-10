package crichton.runner;

import crichton.executors.CommandBuilder;
import crichton.executors.ProcessRunner;
import crichton.paths.DirectoryPaths;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class CliRunner {

    private final String sourcePath;
    private final String reportPath;

    public CliRunner(String sourcePath) {
        this.sourcePath = sourcePath;
        this.reportPath = DirectoryPaths
                .generateUnitReportFilePath(FilenameUtils.getBaseName(sourcePath)).toString();
        deleteReport();
    }

    private CommandBuilder getCliRunnerCommand() {
        CommandBuilder command = new CommandBuilder();
        command.addOption("coyoteCli");
        command.addOption("-n", sourcePath);
        command.addOption("-o", reportPath);
        return command;
    }

    private static CommandBuilder getCliProgressCommand() {
        CommandBuilder command = new CommandBuilder();
        command.addOption("coyoteCli");
        command.addOption("-s");
        return command;
    }

    public void run() throws Exception{
       ProcessRunner.RunResult runResult =
               ProcessRunner.run(System.getProperty("user.home"), getCliRunnerCommand());

       if (runResult.getExitCode() !=0 ) throw new RuntimeException();
    }

    public static String runProgress() throws Exception {
        ProcessRunner.RunResult runResult =
                ProcessRunner.run(System.getProperty("user.home"), getCliProgressCommand());
        if (runResult.getExitCode() != 0) {
            throw new RuntimeException();
        }else {
            return runResult.getOutput();
        }
    }


    public String getReportPath() {
        return reportPath;
    }

    private void deleteReport() {
        File file = new File(reportPath);
        if (file.exists())
            file.delete();
    }
}
