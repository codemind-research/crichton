package org.crichton.models.defect;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UnitTestDefectInfo {

    public static class DefectCode {
        public static final String DIVISION_BY_ZERO = "Div_Zero";
        public static final String SEGFAULTS = "SegFaults";
        public static final String NULL_ACCESS = "Null_Access";
        public static final String ARRAY_OUT_OF_BOUND = "ArrayOutOfBound";
        public static final String ASSERTS = "Asserts";
        public static final String TIMEOUTS = "TimeOuts";
        public static final String FAILURE_FACTORS = "Failure Factors";

        private DefectCode() {
            throw new AssertionError();
        }
    }

    @Getter
    private final String functionName;

    private final Map<String, Integer> defectOccurrences = new HashMap<>();


    public UnitTestDefectInfo(String functionName) {
        this(functionName, 0, 0, 0, 0, 0, 0, 0);
    }

    @Builder
    public UnitTestDefectInfo(String functionName,
                              int divisionByZeroCount,
                              int segFaultsCount,
                              int nullAccessCount,
                              int assertsCount,
                              int timeoutsCount,
                              int arrayOutOfBoundCount,
                              int failureFactorsCount) {
        this.functionName = functionName;
        init(divisionByZeroCount, segFaultsCount, nullAccessCount, assertsCount, timeoutsCount, arrayOutOfBoundCount, failureFactorsCount);
    }

    public void init(int divisionByZeroCount,
                     int segFaultsCount,
                     int nullAccessCount,
                     int assertsCount,
                     int timeoutsCount,
                     int arrayOutOfBoundCount,
                     int failureFactorsCount) {
        put(DefectCode.DIVISION_BY_ZERO, divisionByZeroCount);
        put(DefectCode.SEGFAULTS, segFaultsCount);
        put(DefectCode.NULL_ACCESS, nullAccessCount);
        put(DefectCode.ARRAY_OUT_OF_BOUND, arrayOutOfBoundCount);
        put(DefectCode.ASSERTS, assertsCount);
        put(DefectCode.TIMEOUTS, timeoutsCount);
        put(DefectCode.FAILURE_FACTORS, failureFactorsCount);
    }


    public void put(String key, int value) {
        defectOccurrences.put(key, value);
    }

    public int get(String key) {
        return defectOccurrences.get(key);
    }

    public int getOrDefault(String key, int defaultValue) {
        return defectOccurrences.getOrDefault(key, defaultValue);
    }

    public boolean containsKey(String key) {
        return defectOccurrences.containsKey(key);
    }

    public boolean isDefectFound() {
        return defectOccurrences.entrySet().stream()
                .map(Map.Entry::getValue)
                .anyMatch(count -> count > 0);
    }

    public String getDetectedDefectCodes() {
        return defectOccurrences.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));
    }
}
