package crichton.runner;

import crichton.executors.CommandBuilder;
import crichton.executors.ProcessRunner;

import java.io.File;

public class CliRunner {

    private final String sourcePath;

    public CliRunner(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    private CommandBuilder getCliRunnerCommand() {
        CommandBuilder command = new CommandBuilder();
        command.addOption("coyoteCli");
        command.addOption("-n", sourcePath);
        return command;
    }

    public void run() throws Exception{
       ProcessRunner.RunResult runResult =
               ProcessRunner.run(System.getProperty("user.home"), getCliRunnerCommand());

       if (runResult.getExitCode() !=0 ) throw new RuntimeException();
    }

    public boolean checkSourcePath() {
        return !sourcePath.isBlank() && new File(sourcePath).exists();
    }



}
