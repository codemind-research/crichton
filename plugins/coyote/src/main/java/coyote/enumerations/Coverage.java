package coyote.enumerations;

import coyote.report.Information;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public enum Coverage {

    LINE("ExecutedLines", "Lines", "Line_Coverage","NL"),
    BRANCH("ExecutedBranches","Branches","Branch_Coverage","NB"),
    MCDC("ExecutedPairs","Pairs","MC/DC_Coverage","NP");

    private final String execute;
    private final String total;
    private final String coverage;
    private final String notExist;

    Coverage(String execute, String total, String coverage, String notExist) {
        this.execute = execute;
        this.total = total;
        this.coverage = coverage;
        this.notExist = notExist;
    }

    public String getCoverage() {
        return coverage;
    }

    public String getExecute() {
        return execute;
    }

    public String getTotal() {
        return total;
    }

    public String getNotExist() {
        return notExist;
    }

    public static void convertCoverage (Information<LinkedHashMap<String,Object>> information) {
        Arrays.stream(Coverage.values()).toList().forEach( coverage -> {
            String executeValue = information.getInfo().get(coverage.getExecute()).toString();
            String totalValue = information.getInfo().get(coverage.getTotal()).toString();
            String coverageValue = information.getInfo().get(coverage.getCoverage()).toString();
            String concatenatedValue = coverageValue.equals(coverage.getNotExist()) ? coverageValue :
                    coverageFormat(executeValue,totalValue,coverageValue);
            information.getInfo().remove(coverage.getExecute());
            information.getInfo().remove(coverage.getTotal());
            information.getInfo().put(coverage.getCoverage(),concatenatedValue);
        });
    }

    public static void convertCoverageOfList (Information<List<LinkedHashMap<String,Object>>> information) {
        Arrays.stream(Coverage.values()).toList().forEach(coverage -> {
            information.getInfo().forEach( info -> {
                String executeValue = info.get(coverage.getExecute()).toString();
                String totalValue = info.get(coverage.getTotal()).toString();
                String coverageValue = info.get(coverage.getCoverage()).toString();
                String concatenatedValue = coverageValue.equals(coverage.getNotExist()) ? coverageValue :
                        coverageFormatAddPercent(executeValue,totalValue,coverageValue);
                info.remove(coverage.getExecute());
                info.remove(coverage.getTotal());
                info.put(coverage.getCoverage(),concatenatedValue);
            });
        });
    }

    private static String coverageFormat(String execute, String total, String coverage) {
        return String.format("%s/%s(%s)",execute,total,coverage);
    }

    private static String coverageFormatAddPercent(String execute, String total, String coverage) {
        return String.format("%s/%s(%s%%)",execute,total,coverage);
    }

}
