package injector.enumerations;

import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.nio.file.Paths;
import java.util.EnumSet;

public enum InjectorBinaries {

    DEFECT("engines/DefectInjector.dll"),
    INJECTION("engines/InjectionTester.dll");

    private final String fileName;

    InjectorBinaries(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public static boolean allFilesExistInResources()  {
        ClassLoader classLoader = InjectorBinaries.class.getClassLoader();
        return EnumSet.allOf(InjectorBinaries.class).stream()
                      .allMatch(binaries -> {
                          URL resourceUrl = classLoader.getResource(binaries.getFileName());
                          return resourceUrl != null;
                      });
    }

    public static String getFileInResources(String libraryPath, InjectorBinaries binaries) {
        if(StringUtils.isBlank(libraryPath)) {
            return getFileInResources(binaries);
        }
        else {
            return Paths.get(libraryPath, binaries.getFileName()).normalize().toAbsolutePath().toString();
        }
    }

    public static String getFileInResources(InjectorBinaries binaries) {
        ClassLoader classLoader = InjectorBinaries.class.getClassLoader();
        return classLoader.getResource(binaries.getFileName()).getPath();
    }
}
