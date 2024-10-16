package org.crichton.util;

import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

@Component
public class OperationSystemUtil {

    private final OperatingSystem os;

    public OperationSystemUtil() {
        // SystemInfo 인스턴스를 한 번만 생성
        SystemInfo systemInfo = new SystemInfo();
        this.os = systemInfo.getOperatingSystem();
    }

    public boolean isWindows() {
        return os.getFamily().toLowerCase().contains("windows");
    }

    public boolean isLinux() {
        return os.getFamily().toLowerCase().contains("linux");
    }

    public boolean isMac() {
        return os.getFamily().toLowerCase().contains("mac");
    }

    public String getOsFamily() {
        return os.getFamily();
    }
}
