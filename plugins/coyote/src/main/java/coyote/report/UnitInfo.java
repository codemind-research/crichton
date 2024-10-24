package coyote.report;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.IntStream;

public class UnitInfo extends Information<List<LinkedHashMap<String,Object>>> {

    public static class FieldKey {
        public static final String FILE_PATH = "FilePath";
        public static final String FUNCTION_NAME = "FunctionName";
        public static final String TEST_CASES = "TestCases";
        public static final String EXECUTED_STATEMENTS = "ExecutedStatements";
        public static final String STATEMENTS = "Statements";
        public static final String EXECUTED_BRANCHES = "ExecutedBranches";
        public static final String BRANCHES = "Branches";
        public static final String EXECUTED_PAIRS = "ExecutePairs";
        public static final String PAIRS = "Pairs";

        public static final String STATEMENTS_COVERAGE = "Statements_Coverage";
        public static final String BRANCHES_COVERAGE = "Branches_Coverage";
        public static final String MC_DC_COVERAGE = "MC/DC_Coverage";
    }

    protected UnitInfo(List<String> unitLines) {
        super(new ArrayList<>());
        parser(unitLines);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addInfo(Object... values) {
        getInfo().add((LinkedHashMap<String,Object>) values[0]);
    }


    @Override
    protected void parser(List<String> lines) {
        List<List<String>> convertList =
                lines.stream()
                     .filter(StringUtils::isNotBlank)
                     .map(pl -> Arrays.stream(pl.split(","))
                                      .toList())
                     .filter(pl->!pl.isEmpty())
                     .toList();
        convert(convertList);
    }

    @Override
    protected void convert(List<List<String>> lists) {
        List<String> column = lists.get(0);
        String filename = "";
        for (int i = 1; i < lists.size(); i++) {
            LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
            List<String> currentList = lists.get(i);
            filename = currentList.get(0).isEmpty() ? filename: (currentList.get(0));
            hashMap.put(column.get(0).replace(" ", ""), filename);
            IntStream.range(1, currentList.size())
                     .forEach(index -> {
                         hashMap.put(removeSpacingAndMCDC(column.get(index)), currentList.get(index));
                     });
            addInfo(hashMap);
        }
    }
}
