package crichton.runner;

import crichton.paths.DirectoryPaths;
import crichton.util.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.File;
import java.util.Optional;

public class UnitTestRunner extends ProcessRunner{

    private final String sourcePath;
    private final String dirname;
    private final File reportFile;
    private final File settingFile;

    public UnitTestRunner(String sourcePath, Optional<MultipartFile> optionalSettings) {
        super();
        this.sourcePath = sourcePath;
        this.dirname = FilenameUtils.getBaseName(sourcePath);
        this.reportFile = DirectoryPaths
                .generateUnitReportFilePath(dirname).toFile();
        this.settingFile = DirectoryPaths.generateSettingsPath(dirname).toFile();
        optionalSettings.ifPresent(settings -> FileUtils.readMultipartFile(settings, settingFile));
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
        command.addOption("-o", reportFile.getAbsolutePath());
        command.checkAndAddOption("-p", settingFile.getAbsolutePath(), settingFile::exists);
        return command;
    }

    private void deleteReport() {
        if (reportFile.exists())
            reportFile.delete();
    }

    public boolean isSuccessUnitTest() {
        return reportFile.exists();
    }
}
