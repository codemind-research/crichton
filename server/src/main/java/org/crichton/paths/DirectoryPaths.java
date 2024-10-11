package org.crichton.paths;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryPaths {

    public static final Path USER_PATH = Paths.get(System.getProperty("user.home"));
    public static final Path CRICHTON_PATH = USER_PATH.resolve(".crichton");
    public static final Path UPLOAD_PATH = CRICHTON_PATH.resolve("source");

    static {
        File crichtonDir = CRICHTON_PATH.toFile();
        if (!crichtonDir.exists())
            crichtonDir.mkdir();
    }

    public static Path generateUnzipPath(String sourceName) {
        return UPLOAD_PATH.resolve(sourceName);
    }

    public static Path generateZipPath(String zipPath) {
        return UPLOAD_PATH.resolve(zipPath);
    }


}
