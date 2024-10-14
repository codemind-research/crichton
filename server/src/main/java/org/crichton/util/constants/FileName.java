package org.crichton.util.constants;

public class FileName {
    public static final String TEST_SPEC = "test_spec.json";
    public static final String DEFECT_SPEC = "defect_spec.json";
    public static final String SAFE_SPEC = "safe_spec.json";
    public static final String UNIT_TEST_SPEC = "unit_test_spec.json";


    public static final String DEFECT_INJECTOR = "DefectInjector.dll";

    public static final String DEFECT_SIMULATION_SOURCE = "defectSim.c";
    public static final String DEFECT_SIMULATION_OIL = "defectSim.oil";
    public static final String DEFECT_SIMULATION_EXE = "defectSim_exe";


    public static final String INJECTION_RESULT = "injection_result.csv";

    private FileName() {
        throw new AssertionError("Cannot be instantiated");
    }
}
