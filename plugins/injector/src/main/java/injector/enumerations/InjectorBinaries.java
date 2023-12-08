package injector.enumerations;

import java.net.URL;
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

    public static String getFileInResources(InjectorBinaries binaries) {
        ClassLoader classLoader = InjectorBinaries.class.getClassLoader();
        return classLoader.getResource(binaries.getFileName()).getPath();
    }
}
