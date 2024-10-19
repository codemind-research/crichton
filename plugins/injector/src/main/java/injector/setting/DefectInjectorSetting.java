package injector.setting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import injector.enumerations.InjectorBinaries;
import injector.utils.InjectorProperties;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import runner.paths.PluginPaths;
import runner.util.FileUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

@Getter
public class DefectInjectorSetting {

    public static class ConfigurationKey {
        public static final String WORKSPACE = "workspace";
        public static final String OLI = "oil";
        public static final String GOIL_PROCESS_PATH = "goil.process.path";
        public static final String TEST_SPEC_FILE_PATH = "test";
        public static final String DEFECT_SPEC_FILE_PATH = "defect";
        public static final String SAFE_SPEC_FILE_PATH = "safe";
        public static final String PROPERTIES_PATH = "properties";
        public static final String LIBRARIES_PATH = "libs";
    }

    private final String pluginName;
    private final String oilFile;
    private final String testSpecFile;
    private final String defectSpecFile;
    private final String safeSpecFile;

    private final File projectWorkspace;
    private final File defectDir;
    private final InjectorProperties properties;

    private int defectLength;

    public DefectInjectorSetting(String pluginName, Map<String, String> defectInjectorConfiguration) {
        this.pluginName = pluginName;
        var projectDir = defectInjectorConfiguration.getOrDefault(ConfigurationKey.WORKSPACE, PluginPaths.generatePluginSettingsPath(pluginName).toString());
        this.projectWorkspace = new File(projectDir);
        this.oilFile = defectInjectorConfiguration.getOrDefault(ConfigurationKey.OLI,"");
        this.testSpecFile = defectInjectorConfiguration.getOrDefault(ConfigurationKey.TEST_SPEC_FILE_PATH,"");
        this.defectSpecFile = defectInjectorConfiguration.getOrDefault(ConfigurationKey.DEFECT_SPEC_FILE_PATH,"");
        this.safeSpecFile = defectInjectorConfiguration.getOrDefault(ConfigurationKey.SAFE_SPEC_FILE_PATH,"");
        this.defectDir = Paths.get(projectWorkspace.toString(), "defect").toFile();

        var propertiesDir = defectInjectorConfiguration.getOrDefault(ConfigurationKey.PROPERTIES_PATH, PluginPaths.generatePluginSettingsPath(pluginName).toString());
        this.properties = InjectorProperties.loadProperties(propertiesDir);

    }

    public void makeDefectJson() throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        var defectJsonFile = getDefectSpecFile();
        JsonNode jsonNode = objectMapper.readTree(defectJsonFile);
        if (!defectDir.exists())
            defectDir.mkdir();

        Iterator<JsonNode> elements = jsonNode.elements();
        int id = 1;

        while (elements.hasNext()) {
            JsonNode element = elements.next();
            String outputFileName = "defect_" + id + ".json";
            objectMapper.writeValue(new File(defectDir + File.separator + outputFileName), element);
            id++;
        }
        defectLength = id;
    }

    public void moveCRCFile(String targetSource, int id) {
        String crcFile =  targetSource + File.separator + getTargetCRC(id);
        FileUtils.moveFile(crcFile, projectWorkspace.toString());
    }

    public String getTestSpecFile() {
        return Paths.get(defectDir.getAbsolutePath(), testSpecFile).normalize().toAbsolutePath().toString();
    }

    public String getDefectSpecFile() {
        return Paths.get(defectDir.getAbsolutePath(), defectSpecFile).normalize().toAbsolutePath().toString();
    }

    public String getSafeSpecFile() {
        return Paths.get(defectDir.getAbsolutePath(), safeSpecFile).normalize().toAbsolutePath().toString();
    }


    public File getOilFile() {
        return Paths.get(projectWorkspace.toString(), oilFile).toFile();
    }

    @Deprecated
    public File getOilCrOilFile() {
        return Paths.get(getOilFile().toString()+".cr.oil").toFile();
    }

    public String getTrampoline() {
        var trampolinePath = properties.getTrampolinePath();
        return Paths.get(trampolinePath).normalize().toFile().getAbsolutePath();
    }

    public String getDefectInjectorEngine() {
        return getEngine(InjectorBinaries.DEFECT);
    }

    public String getInjectionTesterEngine() {
        return getEngine(InjectorBinaries.INJECTION);
    }

    public String getEngine(InjectorBinaries binaries) {
        var enginePath = properties.getEnginePath(binaries);
        var engineFile = Paths.get(enginePath).normalize().toFile();
        if(!engineFile.exists()) {
            return binaries.getFileInResources();
        }
        return engineFile.getAbsolutePath();
    }

    public String getGoilProcess() {
        var goilProcessPath = properties.getGoilProcessPath();
        return Paths.get(goilProcessPath).normalize().toFile().getAbsolutePath();
    }

    public String getGoilTemplates() {
        var goilTemplatesPath = properties.getGoilTemplatePath();
        return Paths.get(goilTemplatesPath).normalize().toFile().getAbsolutePath();
    }

    public int getDefectLength() {
        return defectLength;
    }

    public String getTarget(int id) {
        try {
            File Json = new File(getDefectNumberJson(id));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(Json);
            return rootNode.get("target").asText();
        }catch (Exception e){
            return "";
        }
    }

    public String getTargetCRC(int id) {
        return getTarget(id)+".cr.c";
    }

    public String getDefectNumberJson(int id) {
        String outputFileName = "defect_" + id + ".json";
        return new File(defectDir + File.separator + outputFileName).getAbsolutePath();
    }

    public File getProjectWorkspace() {
        return projectWorkspace;
    }

    public String getOutputFilePath() {
        return this.defectDir.toPath().resolve("report.csv").normalize().toFile().getAbsolutePath().toString();
    }

    public String getOutputName(int id) {
        String fileName = FilenameUtils.getBaseName(getTarget(id));
        return fileName + "_report_" + id + ".csv";
    }

    public String getGoilProcessPath() {
        return properties.getProperty(ConfigurationKey.GOIL_PROCESS_PATH, "/usr/bin/goil");
    }


    public File getOutputFile(int id) {
        return Paths.get(projectWorkspace.getAbsolutePath(), getOutputName(id)).toFile();
    }

    public String getExeBinaryFilePath() {
        return this.defectDir.toPath().resolve("defectSim_exe").normalize().toFile().getAbsolutePath().toString();
    }

    public String getExeBinary(int id) {
        String fileName = FilenameUtils.getBaseName(getTarget(id)) + "_exe";
        return Paths.get(projectWorkspace.toString(), fileName).toFile().getAbsolutePath();
    }

    public String getViperPath() {

        var viperPath = System.getenv("VIPER_PATH");
        if(StringUtils.isBlank(viperPath)) {
            viperPath = properties.getViperPath();
        }

        return Paths.get(viperPath).toFile().getAbsolutePath();
    }

    public String getPluginName() {
        return pluginName;
    }
}
