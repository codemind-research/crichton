package coyote.enumerations;

import coyote.report.Information;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public enum RemoveType {


    MISC_FAILED_UNITS("MiscFailedUnits"),
    USER_TESTCASE("userTestcase"),
    NOT_EXECUTED_FILES("NotExecutedFiles"),
    SELECTED_UNITS("SelectedUnits"),
    ASM_FAILED_UNITS("ASMFailedUnits"),
    NOT_EXECUTED_UNITS("NotExecutedUnits"),
    NO_UNIT_FILES("NoUnitFiles"),
    UNIT_SETTING("unitSetting");


    private final String type;

    RemoveType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static void removeTypeInformation (Information<LinkedHashMap<String,Object>> information) {
        Arrays.stream(RemoveType.values()).toList().forEach(coverage -> {
            information.getInfo().remove(coverage.getType());
        });
    }


}
