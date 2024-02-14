package coyote.enumerations;

import coyote.report.Information;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public enum Total {

    FILE("SuccessfulFiles","FailedFiles", "Files"),
    UNIT("SuccessfulUnits","FailedUnits", "Units");

    private final String success;
    private final String fail;
    private final String total;

    Total(String success, String fail, String total) {
        this.success = success;
        this.fail = fail;
        this.total = total;
    }

    public String getTotal() {
        return total;
    }

    public String getFail() {
        return fail;
    }

    public String getSuccess() {
        return success;
    }

    public static void convertTotal (Information<LinkedHashMap<String,Object>> information) {
        Arrays.stream(Total.values()).toList().forEach(total -> {
            String successValue = information.getInfo().get(total.getSuccess()).toString();
            String failValue = information.getInfo().get(total.getFail()).toString();
            String concatenatedValue = totalFormat(successValue,failValue);
            information.getInfo().remove(total.getSuccess());
            information.getInfo().remove(total.getFail());
            information.getInfo().put(total.getTotal(), concatenatedValue);
        });
    }

    private static String totalFormat(String success, String fail) {
        return String.format("%s succ %s fail",success,fail);
    }


}
