package coyote.report;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class TotalInfo extends Information<LinkedHashMap<String,String>> {

    protected TotalInfo(List<String> totalLines) {
        super(new LinkedHashMap<>());
        parser(totalLines);
    }

    @Override
    protected void addInfo(Object... values) {
        getInfo().put(values[0].toString(),values[1].toString());
    }

    protected void parser(List<String> lines) {
        List<List<String>> convertList =
                lines.stream()
                     .filter(StringUtils::isNotBlank)
                     .map(this::removeSpacingAndMCDC)
                     .map(pl -> Arrays.stream(pl.split(","))
                                      .toList()
                                      .stream().toList())
                     .filter(pl->!pl.isEmpty())
                     .toList();
        convert(convertList);
    }

    @Override
    protected void convert(List<List<String>> lists) {
        for (int i = 0; i < lists.size() - 1; i+=2) {
            List<String> currentList = lists.get(i);
            List<String> nextList = lists.get(i + 1);
            for (int j = 0; j < Math.min(currentList.size(), nextList.size()); j++) {
                String key = currentList.get(j);
                String value = nextList.get(j);
                if (!value.isBlank() && !key.isBlank() && !value.equals("Total")) addInfo(key,value);
            }
        }
    }
}
