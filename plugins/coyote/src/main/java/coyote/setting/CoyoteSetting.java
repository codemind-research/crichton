package coyote.setting;

import java.util.Map;

public class CoyoteSetting {

    private String report;
    private String projectSetting;

    public CoyoteSetting(Map<String, String> coyoteSetting) {
        this.report = coyoteSetting.getOrDefault("report","");
        this.projectSetting = coyoteSetting.getOrDefault("projectSetting","");
    }

    public String getReport() {
        return report;
    }

    public String getProjectSetting() {
        return projectSetting;
    }
}
