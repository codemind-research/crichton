package coyote.report;

import coyote.enumerations.Coverage;
import coyote.enumerations.TestCase;

import java.util.*;
import java.util.stream.Collectors;

public class ProjectInfo extends Information<LinkedHashMap<String,Object>>{

    protected ProjectInfo(List<String> projectLines) {
        super(new LinkedHashMap<>());
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

    public void generateTestCaseMap () {
        final LinkedHashMap<String, Object> testcaseMap = new LinkedHashMap<>();
        final Map<String, Object> info = getInfo();
        Arrays.stream(TestCase.values()).toList().forEach( testCase -> {
            String key = testCase.getType();
            String value = info.get(key).toString();
            info.remove(key);
            testcaseMap.put(key,value);
        });

        info.put("testcase", testcaseMap);
    }

}
