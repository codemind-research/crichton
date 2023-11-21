package crichton.infrastructure.csv;

import crichton.domian.dtos.ReportDTO;
import crichton.util.RegexPatterns;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Component("CsvParser")
public class CsvParser {

    private static final int INITIAL_INDEX = 0;

    private int findIndex(List<String> lines, Pattern pattern, int startIdx) {
        return lines.indexOf(lines.stream()
                                  .skip(startIdx)
                                  .filter(pattern.asPredicate())
                                  .findFirst()
                                  .orElse(null));
    }

    public ReportDTO.DataResponse parser(String csvData) {
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

        return ReportDTO.DataResponse.builder().project(projectInfo).file(fileInfo).unit(unitInfo).build();
    }


    private int findProjectIndex(List<String> lines) {
        return findIndex(lines, RegexPatterns.PROJECT_PATTERN, INITIAL_INDEX);
    }

    private int findFileIndex(List<String> lines) {
        return findIndex(lines, RegexPatterns.FILE_PATTERN, INITIAL_INDEX);
    }

    private int findFileTotalIndex(List<String> lines) {
        return findIndex(lines, RegexPatterns.TOTAL_PATTERN, findFileIndex(lines));
    }
    private int findUnitIndex(List<String> lines) {
        return findIndex(lines, RegexPatterns.UNIT_PATTERN, findFileTotalIndex(lines));
    }
    private int findUnitTotalIndex(List<String> lines) {
        return findIndex(lines, RegexPatterns.TOTAL_PATTERN, findUnitIndex(lines));
    }

}
