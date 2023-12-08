package crichton.Infrastructure.store;

import org.springframework.stereotype.Component;
import runner.dto.ProcessedReportDTO;

import java.util.LinkedHashMap;
import java.util.LinkedList;

@Component("TestResult")
public class TestResultMemoryStorage {
    private final LinkedList<ProcessedReportDTO> testResults;

    public TestResultMemoryStorage() {
        this.testResults = new LinkedList<>();
    }

    public void storeTestResult(ProcessedReportDTO result) {
        testResults.add(result);
    }

    public ProcessedReportDTO getTestResult(String pluginName) {
        return testResults.stream()
                          .filter(result -> result.getPluginName().equals(pluginName))
                          .findAny().orElseGet(ProcessedReportDTO::new);
    }

    public LinkedList<ProcessedReportDTO> getAllTestResults() {
        return new LinkedList<>(testResults);
    }
}
