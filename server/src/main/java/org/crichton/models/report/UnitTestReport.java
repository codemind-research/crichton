package org.crichton.models.report;

import lombok.Builder;
import org.crichton.models.defect.UnitTestDefectInfo;

import java.util.List;

@Builder
public record UnitTestReport(String file, List<UnitTestDefectInfo> defectInfos) {
    public static class Key {
        public static final String FILE = "file";
        public static final String UNIT = "unit";
        public static final String FUNCTION_NAME = "FunctionName";
        public static final String FILE_PATH = "File Path";
        public static final String NULL_ACCESS = "Null_Access";
        public static final String SEGFAULTS = "SegFaults";
        public static final String DIV_ZERO = "Div_Zero";
        public static final String ARRAY_OUT_OF_BOUND = "ArrayOutOfBound";
        public static final String ASSERTS = "Asserts";
        public static final String TIMEOUTS = "TimeOuts";
        public static final String FAILURE_FACTORS = "Failure Factors";
    }
}
