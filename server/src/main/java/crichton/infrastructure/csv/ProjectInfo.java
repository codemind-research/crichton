package crichton.infrastructure.csv;

import java.util.HashMap;
import java.util.List;

public class ProjectInfo extends Information<HashMap<String,String>>{

    protected ProjectInfo(List<String> projectLines) {
        super(new HashMap<>());
        parser(projectLines);
    }

    @Override
    protected void addInfo(Object... values) {
        getInfo().put(values[0].toString(),values[1].toString());
    }

    @Override
    protected void convert(List<List<String>> lists) {
        for (int i = 0; i < lists.size() - 1; i+=2) {
            List<String> currentList = lists.get(i);
            List<String> nextList = lists.get(i + 1);
            for (int j = 0; j < Math.min(currentList.size(), nextList.size()); j++) {
                if (i == 0 && j == 0) {
                    addInfo(currentList.get(j), currentList.get(j+1));
                    addInfo(nextList.get(j),nextList.get(j+1));
                    j+=1;
                }else {
                    String key = currentList.get(j);
                    String value = nextList.get(j);
                    addInfo(key,value);
                }
            }
        }
    }

}
