package injector.setting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import runner.paths.PluginPaths;
import runner.util.FileUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

public class DefectInjectorSetting {

    private String pluginName;
    private String oilFile;
    private String defectSpecFile;
    private String safeSpecFile;
    private String trampoline;
    private int defectLength;
    private File defectDir;
    private final File pluginSettingDir;

    public DefectInjectorSetting(String pluginName, Map<String, String> defectInjectorSetting) {
        this.pluginName = pluginName;
        var projectDir = defectInjectorSetting.getOrDefault("project", PluginPaths.generatePluginSettingsPath(pluginName).toString());
        this.pluginSettingDir = new File(projectDir);
        this.oilFile = defectInjectorSetting.getOrDefault("oil","");
        this.defectSpecFile = defectInjectorSetting.getOrDefault("defect","");
        this.safeSpecFile = defectInjectorSetting.getOrDefault("safe","");
        this.trampoline = defectInjectorSetting.getOrDefault("trampoline","");
        this.defectDir = Paths.get(pluginSettingDir.toString(), "defect").toFile();
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
        FileUtils.moveFile(crcFile,pluginSettingDir.toString());
    }


    public File getOilFile() {
        return Paths.get(pluginSettingDir.toString(), oilFile).toFile();
    }


    public File getOilCrOilFile() {
        return Paths.get(getOilFile().toString()+".cr.oil").toFile();
    }

    public File getTestSpecFile() {
        return Paths.get(pluginSettingDir.toString(), defectSpecFile).toFile();
    }

    public File getDefectSpecFile() {
        return Paths.get(pluginSettingDir.toString(), defectSpecFile).toFile();
    }

    public File getSafeSpecFile() {
        return Paths.get(pluginSettingDir.toString(), safeSpecFile).toFile();
    }

    public String getTrampoline() {
        return this.trampoline;
    }

    public String getGoilTemplates() {
        return Paths.get(this.trampoline, "goil" , "templates").toFile().getAbsolutePath();
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

    public File getPluginSettingDir() {
        return pluginSettingDir;
    }

    public String getOutputName(int id) {
        String fileName = FilenameUtils.getBaseName(getTarget(id));
        return fileName + "_report_" + id + ".csv";
    }

    public File getOutputFile(int id) {
        return Paths.get(pluginSettingDir.getAbsolutePath(), getOutputName(id)).toFile();
    }

    public String getExeBinary(int id) {
        String fileName = FilenameUtils.getBaseName(getTarget(id)) + "_exe";
        return Paths.get(pluginSettingDir.toString(), fileName).toFile().getAbsolutePath();
    }

    public String getViperPath() {
        return Paths.get(trampoline,"viper").toFile().getAbsolutePath();
    }

    public String getPluginName() {
        return pluginName;
    }
}
