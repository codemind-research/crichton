package injector.setting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import injector.enumerations.InjectorBinaries;
import injector.utils.InjectorPluginProperties;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import runner.paths.PluginPaths;
import runner.setting.PluginSetting;
import runner.util.FileUtils;
import runner.util.constants.FileName;
import runner.util.constants.PluginConfigurationKey;

import java.io.File;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

@Getter
public class DefectInjectorSetting extends PluginSetting {

    private final String testSpecFile;
    private final String defectSpecFile;
    private final String safeSpecFile;

    private final String defectSimulationOilFileName;
    private final String defectSimulationExeFileName;

    private final File defectDir;
    private final InjectorPluginProperties properties;

    private int defectLength;

    public DefectInjectorSetting(String pluginName, Map<String, String> defectInjectorConfiguration) {
        super(pluginName, defectInjectorConfiguration);

        this.defectDir = Paths.get(this.workingDirectory.getAbsolutePath(), "defect").toFile();

        this.testSpecFile = defectInjectorConfiguration.getOrDefault(PluginConfigurationKey.DefectInjector.TEST_SPEC_FILE_NAME,"");
        this.defectSpecFile = defectInjectorConfiguration.getOrDefault(PluginConfigurationKey.DefectInjector.DEFECT_SPEC_FILE_NAME,"");
        this.safeSpecFile = defectInjectorConfiguration.getOrDefault(PluginConfigurationKey.DefectInjector.SAFE_SPEC_FILE_NAME,"");

        this.defectSimulationOilFileName = defectInjectorConfiguration.getOrDefault(PluginConfigurationKey.DefectInjector.DEFECT_SIMULATION_OIL_FILE_NAME,"defectSim.oil");
        this.defectSimulationExeFileName = defectInjectorConfiguration.getOrDefault(PluginConfigurationKey.DefectInjector.DEFECT_SIMULATION_EXE_FILE_NAME,"defectSim_exe");

        var propertiesDir = defectInjectorConfiguration.getOrDefault(PluginConfigurationKey.PROPERTIES_PATH, PluginPaths.generatePluginSettingsPath(pluginName).toString());
        this.properties = InjectorPluginProperties.loadProperties(propertiesDir);

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
        FileUtils.moveFile(crcFile, workingDirectory.getAbsolutePath());
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


    public File getDefectSimulationOilFileName() {
        return Paths.get(workingDirectory.getAbsolutePath(), defectSimulationOilFileName).normalize().toFile();
    }

    @Deprecated
    public File getOilCrOilFile() {
        return Paths.get(getDefectSimulationOilFileName().toString()+".cr.oil").toFile();
    }

    public String getExeBinaryFilePath() {
        return Paths.get(workingDirectory.getAbsolutePath(), defectSimulationExeFileName).normalize().toFile().getAbsolutePath();
    }

    public String getExeBinary(int id) {
        String fileName = FilenameUtils.getBaseName(getTarget(id)) + "_exe";
        return Paths.get(workingDirectory.getAbsolutePath(), fileName).toFile().getAbsolutePath();
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

    public String getMakeFilePath(String makeFileName) {
        return Paths.get(this.workingDirectory.getAbsolutePath(), makeFileName).normalize().toFile().getAbsolutePath();
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
        return workingDirectory;
    }

    public String getOutputFilePath() {
        return this.defectDir.toPath().resolve(FileName.INJECTION_RESULT).normalize().toFile().getAbsolutePath().toString();
    }

    public String getOutputName(int id) {
        String fileName = FilenameUtils.getBaseName(getTarget(id));
        return fileName + "_report_" + id + ".csv";
    }


    public File getOutputFile(int id) {
        return Paths.get(workingDirectory.getAbsolutePath(), getOutputName(id)).toFile();
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
