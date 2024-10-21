package coyote.process;

import coyote.setting.CoyoteSetting;
import runner.process.ProcessRunner;
import runner.util.CommandBuilder;

import java.io.File;

public class CoyoteRunner extends ProcessRunner {

    private final String targetSource;
    private final CoyoteSetting setting;

    public CoyoteRunner(String targetSource, CoyoteSetting setting) {
        super();
        this.targetSource = targetSource;
        this.setting = setting;
    }

    @Override
    protected CommandBuilder buildCommand() {

        CommandBuilder command = new CommandBuilder();
        command.addOption(getProcessName());
        command.addOption("-n", targetSource);
        command.addOption("-o", setting.getReportFilePath());
        command.checkAndAddOption("-p", setting.getProjectSettingFilPath(),
                (projectSettingFileName) -> {
                    try {
                        var projectSettingFile = new File(projectSettingFileName);
                        return projectSettingFile.exists() && projectSettingFile.isFile();
                    }
                    catch (NullPointerException e) {
                        return false;
                    }
                    catch (Exception e) {
                        return false;
                    }
                });
        return command;
    }

    @Override
    protected String getProcessName() {
        return setting.getEnginePath();
    }

}
