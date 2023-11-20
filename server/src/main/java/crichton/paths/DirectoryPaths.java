package crichton.paths;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryPaths {

    public static final Path USER_PATH = Paths.get(System.getProperty("user.home"));
    public static final Path CLI_PATH = USER_PATH.resolve("coyoteCli");
    public static final Path UPLOAD_PATH = CLI_PATH.resolve("source");
    public static final Path REPORT_PATH = CLI_PATH.resolve("report");

    public static Path generateUnzipPath(String sourceName) {
        return UPLOAD_PATH.resolve(sourceName);
    }

    public static Path generateZipPath(String zipPath) {
        return UPLOAD_PATH.resolve(zipPath);
    }

    public static Path generateUnitReportFilePath(String sourceName) {
        return REPORT_PATH.resolve(sourceName+"_unitTest.csv");
    }

    public static Path generateInjectionReportFilePath(String sourceName) {
        return REPORT_PATH.resolve(sourceName+"_injectionTest.csv");
    }

}
