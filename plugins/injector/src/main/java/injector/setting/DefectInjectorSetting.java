package injector.setting;

import com.fasterxml.jackson.core.JsonParser;
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
import runner.util.constants.DirectoryName;
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

    private final File defectDirectory;
    private final InjectorPluginProperties properties;

    private final boolean isMultiMode;

    private Integer defectSpecCount = null;

    public DefectInjectorSetting(String pluginName, Map<String, String> configuration) {
        super(pluginName, configuration);

        this.defectDirectory = Paths.get(this.workingDirectory.getAbsolutePath(), configuration.getOrDefault(PluginConfigurationKey.DefectInjector.DIRECTORY_NAME, "defect")).toFile();

        this.testSpecFile = configuration.getOrDefault(PluginConfigurationKey.DefectInjector.TEST_SPEC_FILE_NAME,"");
        this.defectSpecFile = configuration.getOrDefault(PluginConfigurationKey.DefectInjector.DEFECT_SPEC_FILE_NAME,"");
        this.safeSpecFile = configuration.getOrDefault(PluginConfigurationKey.DefectInjector.SAFE_SPEC_FILE_NAME,"");

        this.defectSimulationOilFileName = configuration.getOrDefault(PluginConfigurationKey.DefectInjector.DEFECT_SIMULATION_OIL_FILE_NAME,FileName.DEFECT_SIMULATION_OIL);
        this.defectSimulationExeFileName = configuration.getOrDefault(PluginConfigurationKey.DefectInjector.DEFECT_SIMULATION_EXE_FILE_NAME,FileName.DEFECT_SIMULATION_EXE);

        var processMode = configuration.getOrDefault(PluginConfigurationKey.DefectInjector.DEFECT_MULTI_PROCESS_MODE, String.valueOf(Boolean.TRUE));

        if(Boolean.valueOf(processMode)){
            this.isMultiMode = true;
        }
        else {
            this.isMultiMode = false;
        }

        var propertiesDir = configuration.getOrDefault(PluginConfigurationKey.PROPERTIES_PATH, PluginPaths.generatePluginSettingsPath(pluginName).toString());
        this.properties = InjectorPluginProperties.loadProperties(propertiesDir);

    }

    public void splitDefectSpecFile() throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        var defectJsonFile = getDefectSpecFile();
        JsonNode jsonNode = objectMapper.readTree(new File(defectJsonFile));
        if (!defectDirectory.exists())
            defectDirectory.mkdir();

        var defectJsonFileName = new File(defectJsonFile).getName();
        int id = 1;
        if(jsonNode.isArray()) {

            Iterator<JsonNode> elements = jsonNode.elements();

            while (elements.hasNext()) {
                JsonNode element = elements.next();
                String outputFileName = defectJsonFileName.replace(".json", "_" + id + ".json");
                objectMapper.writeValue(new File(defectDirectory + File.separator + outputFileName), element);

                if(elements.hasNext()) {
                    id++;
                }
            }
        }
        else {
            String outputFileName = defectJsonFileName.replace(".json", "_" + id + ".json");
            objectMapper.writeValue(new File(defectDirectory + File.separator + outputFileName), jsonNode);
        }

        defectSpecCount = id;
    }

    public void moveCRCFile(String targetSource, int id) {
        String crcFile =  targetSource + File.separator + getTargetCRC(id);
        FileUtils.moveFile(crcFile, sourceDirectory.getAbsolutePath());
    }

    public String getTestSpecFile() {
        return Paths.get(defectDirectory.getAbsolutePath(), testSpecFile).normalize().toAbsolutePath().toString();
    }

    public String getDefectSpecFile() {
        return Paths.get(defectDirectory.getAbsolutePath(), defectSpecFile).normalize().toAbsolutePath().toString();
    }

    public String getDefectSpecFile(int id) {
        return Paths.get(defectDirectory.getAbsolutePath(), defectSpecFile.replace(".json", "_" + id + ".json")).normalize().toAbsolutePath().toString();
    }

    public String getSafeSpecFile() {
        return Paths.get(defectDirectory.getAbsolutePath(), safeSpecFile).normalize().toAbsolutePath().toString();
    }


    public File getDefectSimulationOilFileName() {
        return Paths.get(sourceDirectory.getAbsolutePath(), defectSimulationOilFileName).normalize().toFile();
    }

    @Deprecated
    public File getOilCrOilFile() {
        return Paths.get(getDefectSimulationOilFileName().toString()+".cr.oil").toFile();
    }

    public String getExeBinaryFilePath() {
        return Paths.get(sourceDirectory.getAbsolutePath(), defectSimulationExeFileName).normalize().toFile().getAbsolutePath();
    }

    public String getExeBinaryFilePath(int id) {
        String fileName = FilenameUtils.getBaseName(getTarget(id)) + "_exe";
        return Paths.get(sourceDirectory.getAbsolutePath(), fileName).toFile().getAbsolutePath();
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
        return Paths.get(this.sourceDirectory.getAbsolutePath(), makeFileName).normalize().toFile().getAbsolutePath();
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
        String outputFileName = "defect_spec" + id + ".json";
        return defectDirectory.toPath().resolve(outputFileName).toFile().getAbsolutePath();
    }

    public File getProjectWorkspace() {
        return workingDirectory;
    }

    public String getOutputFilePath() {
        return this.defectDirectory.toPath().resolve(this.properties.getReportFileName()).normalize().toFile().getAbsolutePath().toString();
    }

    public String getOutputFilePath(int id) {
        var fileExt = "." + FilenameUtils.getExtension(this.properties.getReportFileName());
        var outputFileName = this.properties.getReportFileName().replace(fileExt, "_" + id + fileExt);
        return this.defectDirectory.toPath().resolve(outputFileName).normalize().toFile().getAbsolutePath().toString();
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
