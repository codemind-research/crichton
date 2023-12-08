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
    private String defectJson;
    private String safeJson;
    private String trampoline;
    private int defectLength;
    private File defectDir;
    private final File pluginSettingDir;

    public DefectInjectorSetting(String pluginName, Map<String, String> defectInjectorSetting) {
        this.pluginName = pluginName;
        this.pluginSettingDir = PluginPaths.generatePluginSettingsPath(pluginName).toFile();
        this.oilFile = defectInjectorSetting.getOrDefault("oil","");
        this.defectJson = defectInjectorSetting.getOrDefault("defect","");
        this.safeJson = defectInjectorSetting.getOrDefault("safe","");
        this.trampoline = defectInjectorSetting.getOrDefault("trampoline","");
        this.defectDir = Paths.get(pluginSettingDir.toString(), "defect").toFile();
    }

    public void makeDefectJson() throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(getDefectJson());
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

    public File getDefectJson() {
        return Paths.get(pluginSettingDir.toString(), defectJson).toFile();
    }

    public File getSafeJson() {
        return Paths.get(pluginSettingDir.toString(), safeJson).toFile();
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

    public String getTitle(int id) {
        try {
            File Json = new File(getDefectNumberJson(id));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(Json);
            return rootNode.get("title").asText();
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
