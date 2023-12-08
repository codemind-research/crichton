package coyote.process;

import lombok.NonNull;
import runner.process.ProcessRunner;
import runner.util.CommandBuilder;

import java.io.File;
import java.util.List;

import static runner.Plugin.OUTPUT_PATH;

public class CoyoteRunner extends ProcessRunner {

    private final String targetSource;
    private File reportFile;
    private File projectSetting;

    public CoyoteRunner(String targetSource, File reportFile, File projectSetting) {
        super();
        this.targetSource = targetSource;
        this.reportFile = reportFile;
        this.projectSetting = projectSetting;
    }

    @Override
    protected CommandBuilder buildCommand() {
        CommandBuilder command = new CommandBuilder();
        command.addOption("coyoteCli");
        command.addOption("-n", targetSource);
        command.addOption("-o", reportFile.getAbsolutePath());
        command.checkAndAddOption("-p", projectSetting.getAbsolutePath(),
                () -> projectSetting.exists() && projectSetting.isFile());
        return command;
    }

}
