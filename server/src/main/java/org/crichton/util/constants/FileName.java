package org.crichton.util.constants;

public class FileName {
    public static final String TEST_SPEC = "test_spec.json";
    public static final String DEFECT_SPEC = "defect_spec.json";
    public static final String SAFE_SPEC = "safe_spec.json";

    private FileName() {
        throw new AssertionError("Cannot be instantiated");
    }
}
