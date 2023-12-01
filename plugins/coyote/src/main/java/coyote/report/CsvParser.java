package coyote.report;

import coyote.util.RegexPatterns;
import runner.dto.ProcessedReportDTO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
        HashMap<String,String> projectInfo = new ProjectInfo(lines.
                subList(findProjectIndex(lines),findFileIndex(lines)))
                .getInfo();

        List<HashMap<String,String>> fileInfo = new FileInfo(lines
                .subList(findFileIndex(lines), findFileTotalIndex(lines))).
                getInfo();

        List<HashMap<String, String>> unitInfo = new UnitInfo(lines
                .subList(findUnitIndex(lines), findUnitTotalIndex(lines))).
                getInfo();

        List<Integer> totalIndex = List.of(findFileIndex(lines),findFileTotalIndex(lines),findUnitIndex(lines), findUnitTotalIndex(lines));
        List<String> totalLines = IntStream.range(0, lines.size())
                                           .filter(totalIndex::contains)
                                           .mapToObj(lines::get)
                                           .toList();
        new TotalInfo(totalLines).getInfo().forEach(projectInfo::putIfAbsent);
        return ProcessedReportDTO.builder().plugin("coyote").build();
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