package org.crichton.util.constants;

public class FileName {

    //#region JSON
    public static final String TEST_SPEC = "test_spec.json";
    public static final String DEFECT_SPEC = "defect_spec.json";
    public static final String SAFE_SPEC = "safe_spec.json";
    public static final String UNIT_TEST_SPEC = "unit_test_spec.json";
    //#endregion

    //#region Plugin
    public static final String INJECTOR_PLUGIN = "injector.jar";
    public static final String UNIT_TESTER_PLUGIN = "coyote.jar";
    public static final String PLUGIN_PROPERTY_FILE = "plugin.properties";
    //#endregion

    public static final String DEFECT_INJECTOR = "DefectInjector.dll";

    public static final String DEFECT_SIMULATION_SOURCE = "defectSim.c";
    public static final String DEFECT_SIMULATION_OIL = "defectSim.oil";
    public static final String DEFECT_SIMULATION_EXE = "defectSim_exe";


    public static final String INJECTION_RESULT = "injection_result.csv";



    private FileName() {
        throw new AssertionError("Cannot be instantiated");
    }
}
