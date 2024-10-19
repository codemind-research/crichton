package org.crichton.util.constants;

public class PluginSettingKey {

    public static final String PROJECT_ID = "project_id";
    public static final String WORKSPACE = "workspace";

    private PluginSettingKey() {
        throw new AssertionError();
    }

    public static class DefectInjector {
        public static final String TEST_SPEC_FILE_NAME = "test";
        public static final String DEFECT_SPEC_FILE_NAME = "defect";
        public static final String SAFE_SPEC_FILE_NAME = "safe";
        public static final String OIL_FILE_NAME = "oil";
        public static final String TRAMPOLINE_PATH = "trampoline";
        public static final String PROPERTIES_PATH = "properties";

    }

    public static class UnitTester {
        public static final String UNIT_TEST_SPEC_FILE_PATH = "unit_test";
    }
}
