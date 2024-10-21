package coyote.process;

import coyote.setting.CoyoteSetting;
import lombok.NonNull;
import runner.process.ProcessRunner;
import runner.util.CommandBuilder;

import java.io.File;
import java.util.List;

import static runner.Plugin.OUTPUT_PATH;

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
        command.addOption("-o", setting.getReport());
        command.checkAndAddOption("-p", setting.getProjectSetting(),
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
