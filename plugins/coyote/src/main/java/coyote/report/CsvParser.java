package coyote.report;

import coyote.enumerations.Coverage;
import coyote.enumerations.RemoveType;
import coyote.enumerations.Total;
import coyote.util.RegexPatterns;
import runner.dto.ProcessedReportDTO;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class CsvParser {

    private static final int INITIAL_INDEX = 0;

    private static int findIndex(List<String> lines, Pattern pattern, int startIdx) {
        return lines.indexOf(lines.stream()
                                  .skip(startIdx)
                                  .filter(pattern.asPredicate())
                                  .findFirst()
                                  .orElse(null));
    }

    public static ProcessedReportDTO parser(String csvData) {
        List<String> lines = Arrays.stream(csvData.split("\n"))
                                   .toList();

        int projectInfoStartIndex = findProjectIndex(lines);
        int projectInfoEndIndex = findFileIndex(lines);
        List<String> projectInfoLines = lines.subList(projectInfoStartIndex, projectInfoEndIndex);
        ProjectInfo projectInfo = new ProjectInfo(projectInfoLines);


        int fileInfoStartIndex = findFileIndex(lines);
        int fileInfoEndIndex = findFileTotalIndex(lines);
        List<String> fileInfoLines = lines.subList(fileInfoStartIndex, fileInfoEndIndex);
        FileInfo fileInfo = new FileInfo(fileInfoLines);

        int unitInfoStartIndex = findUnitIndex(lines);
        int unitInfoEndIndex = findUnitTotalIndex(lines);
        List<String> unitInfoLines = lines.subList(unitInfoStartIndex, unitInfoEndIndex);
        UnitInfo unitInfo = new UnitInfo(unitInfoLines);

        Coverage.convertCoverageOfList(fileInfo);
        Coverage.convertCoverageOfList(unitInfo);
        fileInfo.combineUnitInfo(unitInfo.getInfo());

        List<Integer> totalIndex = List.of(fileInfoStartIndex, fileInfoEndIndex, unitInfoStartIndex, unitInfoEndIndex);
        List<String> totalInfoLines = IntStream.range(0, lines.size())
                                           .filter(totalIndex::contains)
                                           .mapToObj(lines::get)
                                           .toList();
        new TotalInfo(totalInfoLines).getInfo().forEach(projectInfo.getInfo()::putIfAbsent);
        Coverage.convertCoverage(projectInfo);
        Total.convertTotal(projectInfo);
        RemoveType.removeTypeInformation(projectInfo);
        projectInfo.generateTestCaseMap();


        LinkedHashMap<String,Object> info = new LinkedHashMap<>();
        info.put("project",projectInfo.getInfo());
        info.put("file",fileInfo.getInfo());

        return ProcessedReportDTO.builder().pluginName("coyote").info(info).build();
    }

    private static int findProjectIndex(List<String> lines) {
        return findIndex(lines, RegexPatterns.PROJECT_PATTERN, INITIAL_INDEX);
    }

    private static int findFileIndex(List<String> lines) {
        return findIndex(lines, RegexPatterns.FILE_PATTERN, INITIAL_INDEX);
    }

    private static int findFileTotalIndex(List<String> lines) {
        return findIndex(lines, RegexPatterns.TOTAL_PATTERN, findFileIndex(lines));
    }
    private static int findUnitIndex(List<String> lines) {
        return findIndex(lines, RegexPatterns.UNIT_PATTERN, findFileTotalIndex(lines));
    }
    private static int findUnitTotalIndex(List<String> lines) {
        return findIndex(lines, RegexPatterns.TOTAL_PATTERN, findUnitIndex(lines));
    }

}