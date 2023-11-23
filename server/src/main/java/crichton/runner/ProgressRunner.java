package crichton.runner;

import crichton.paths.DirectoryPaths;
import org.zeroturnaround.exec.ProcessExecutor;

public class ProgressRunner extends ProcessRunner{

    @Override
    protected ProcessExecutor createProcessExecutor() {
        return  new ProcessExecutor()
                .directory(DirectoryPaths.USER_PATH.toFile())
                .readOutput(true);
    }

    @Override
    public CommandBuilder builder() {
        CommandBuilder command = new CommandBuilder();
        command.addOption("coyoteCli");
        command.addOption("-s");
        return command;
    }
}
