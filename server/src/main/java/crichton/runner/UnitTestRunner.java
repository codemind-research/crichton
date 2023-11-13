package crichton.runner;

import crichton.paths.DirectoryPaths;
import org.apache.commons.io.FilenameUtils;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.File;

public class UnitTestRunner extends ProcessRunner implements Runner{

    private final String sourcePath;
    private final String reportPath;

    public UnitTestRunner(String sourcePath) {
        super();
        this.sourcePath = sourcePath;
        this.reportPath = DirectoryPaths
                .generateUnitReportFilePath(FilenameUtils.getBaseName(sourcePath)).toString();
        deleteReport();
    }

    @Override
    protected ProcessExecutor createProcessExecutor() {
        return  new ProcessExecutor()
                .directory(DirectoryPaths.USER_PATH.toFile())
                .readOutput(true);
    }


    @Override
    public CommandBuilder builder(){
        CommandBuilder command = new CommandBuilder();
        command.addOption("coyoteCli");
        command.addOption("-n", sourcePath);
        command.addOption("-o", reportPath);
        return command;
    }

    private void deleteReport() {
        File file = new File(reportPath);
        if (file.exists())
            file.delete();
    }

    public boolean isSuccessUnitTest() {
        return new File(reportPath).exists();
    }
}
