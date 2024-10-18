package org.crichton.util.constants;

public class PluginSettingKey {

    public static final String PROJECT_ID = "project_id";
    public static final String PROJECT_DIR_PATH = "project";


    private PluginSettingKey() {
        throw new AssertionError();
    }

    public static class DefectInjector {
        public static final String TEST_SPEC_FILE_PATH = "test";
        public static final String DEFECT_SPEC_FILE_PATH = "defect";
        public static final String SAFE_SPEC_FILE_PATH = "safe";
        public static final String OIL_FILE_PATH = "oil";
        public static final String TRAMPOLINE_PATH = "trampoline";
    }

    public static class UnitTester {
        public static final String UNIT_TEST_SPEC_FILE_PATH = "unit_test";
    }
}
