package runner.util.constants;

public class PluginConfigurationKey {

    public static final String PROJECT_ID = "project_id";
    public static final String WORKSPACE = "workspace";
    public static final String PROPERTIES_PATH = "file.properties";

    private PluginConfigurationKey() {
        throw new AssertionError();
    }

    public static class DefectInjector {
        public static final String TEST_SPEC_FILE_NAME = "file.spec.test";
        public static final String DEFECT_SPEC_FILE_NAME = "file.spec.defect";
        public static final String SAFE_SPEC_FILE_NAME = "file.spec.safe";
        public static final String DEFECT_SIMULATION_OIL_FILE_NAME = "file.defect.simulation.oil";
        public static final String DEFECT_SIMULATION_EXE_FILE_NAME = "file.defect.simulation.execute";
    }

    public static class UnitTester {
        public static final String UNIT_TEST_SETTING_FILE_NAME = "file.setting.unit-tester";
    }

}
