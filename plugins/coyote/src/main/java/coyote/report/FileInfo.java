package coyote.report;

import coyote.enumerations.Coverage;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FileInfo extends Information<List<LinkedHashMap<String,Object>>>{

    protected FileInfo(List<String> fileLines) {
        super(new ArrayList<>());
        parser(fileLines);
    }

    @Override
    protected void addInfo(Object... values) {
        getInfo().add((LinkedHashMap<String, Object>) values[0]);
    }

    @Override
    protected void convert(List<List<String>> lists) {
        List<String> column = lists.get(0);
        for (int i = 1; i < lists.size(); i++) {
            LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
            List<String> currentList = lists.get(i);
            IntStream.range(0, currentList.size())
                     .forEach(index -> {
                         hashMap.put(column.get(index).replaceAll("Files","title"),currentList.get(index));
                     });
            addInfo(hashMap);
        }
    }

    public void combineUnitInfo ( List<LinkedHashMap<String, Object>> unitInfo) {
        Map<Object, List<LinkedHashMap<String, Object>>> groupedData = unitInfo.stream()
                                                                         .collect(Collectors.groupingBy(m -> m.get("FilePath")));

        getInfo().forEach(fileMap -> {
            String files = (String) fileMap.get("title");
            if (groupedData.containsKey(files)) {
                List<LinkedHashMap<String, Object>> group = groupedData.get(files);
                fileMap.putIfAbsent("unit",group);
            }
        });
    }
}
