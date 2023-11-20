package crichton.infrastructure.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

public class FileInfo extends Information<List<HashMap<String,String>>>{

    protected FileInfo(List<String> fileLines) {
        super(new ArrayList<>());
        parser(fileLines);
    }

    @Override
    protected void addInfo(Object... values) {
        getInfo().add((HashMap<String, String>) values[0]);
    }

    @Override
    protected void convert(List<List<String>> lists) {
        List<String> column = lists.get(0);
        for (int i = 1; i < lists.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            List<String> currentList = lists.get(i);
            IntStream.range(0, currentList.size())
                     .forEach(index -> {
                         hashMap.put(column.get(index),currentList.get(index));
                     });
            addInfo(hashMap);
        }
    }
}
