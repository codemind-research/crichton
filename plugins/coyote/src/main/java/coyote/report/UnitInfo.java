package coyote.report;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.IntStream;

public class UnitInfo extends Information<List<LinkedHashMap<String,Object>>> {

    protected UnitInfo(List<String> unitLines) {
        super(new ArrayList<>());
        parser(unitLines);
    }

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
            hashMap.put(column.get(0), filename);
            IntStream.range(1, currentList.size())
                     .forEach(index -> {
                         hashMap.put(removeSpacingAndMCDC(column.get(index)), currentList.get(index));
                     });
            addInfo(hashMap);
        }
    }
}
