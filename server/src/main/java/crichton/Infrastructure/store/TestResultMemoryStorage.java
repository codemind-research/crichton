package crichton.Infrastructure.store;

import org.springframework.stereotype.Component;
import runner.dto.ProcessedReportDTO;

import java.util.LinkedHashMap;
import java.util.LinkedList;

@Component("TestResult")
public class TestResultMemoryStorage {
    private final LinkedHashMap<String, LinkedHashMap<String,Object>> testResults;

    public TestResultMemoryStorage() {
        this.testResults = new LinkedHashMap<>();
    }

    public void storeTestResult(ProcessedReportDTO result) {
        testResults.put(result.getPluginName(), result.getInfo());
    }

    public LinkedHashMap<String,Object> getTestResult(String pluginName) {
        return testResults.getOrDefault(pluginName, new LinkedHashMap<>());
    }

    public LinkedList<ProcessedReportDTO> getAllTestResults() {
        LinkedList<ProcessedReportDTO> allTestResults = new LinkedList<>();
        testResults.forEach( (key, value) -> {
            allTestResults.add(ProcessedReportDTO.builder()
                                                 .pluginName(key)
                                                 .info(value)
                                                 .build());
        });
        return allTestResults;
    }
}
