package injector.report;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import injector.setting.DefectInjectorSetting;
import lombok.Builder;
import lombok.NonNull;
import runner.util.FileUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

public class Parser {

    private final DefectInjectorSetting setting;

    public Parser(@NonNull DefectInjectorSetting setting) {
        this.setting = setting;
    }

    @SuppressWarnings("unchecked")
    public LinkedHashMap<String, Object> convert() {
        File defectJson = Paths.get(setting.getDefectSpecFile()).toFile();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);


            JsonNode rootNode = objectMapper.readTree(defectJson);
            List<Map<String, Object>> defectSpecs = new ArrayList<>();
            if(rootNode.isArray()) {
                defectSpecs = objectMapper.readValue(rootNode.toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, LinkedHashMap.class));
            }
            else {
                var defectSpec = objectMapper.treeToValue(rootNode, LinkedHashMap.class);
                defectSpecs.add(defectSpec);
            }



            LinkedHashMap<String, Object> defectMap = defectSpecs.stream()
                    .collect(LinkedHashMap::new,
                            (map, entry) -> {
                                String key = entry.remove("id").toString();
                                entry.putIfAbsent("safe", getSaveJsonData(Integer.parseInt(key)));
                                map.put(key, entry);
                            },
                            LinkedHashMap::putAll);
            return defectMap;
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }

    }

    private List<LinkedHashMap<String, Object>> getSaveJsonData(int id) {
        try {
            File safeJson = Paths.get(setting.getSafeSpecFile()).toFile();
            File report = Paths.get(setting.getOutputFilePath(id)).toFile();
            ObjectMapper objectMapper = new ObjectMapper();
            List<LinkedHashMap<String, Object>> safeMap = objectMapper
                    .readValue(safeJson, new TypeReference<>() {});
            if (report.exists()){
                List<String> error = convertReportCsv(report);
                safeMap.forEach( map -> {
                    String safeId = map.get("id").toString();
                    if (error.stream().anyMatch( e -> e.equals(safeId)))
                        map.put("isSuccess",false);
                    else
                        map.put("isSuccess",true);
                });
            }else {
                safeMap.forEach( map-> map.put("isSuccess", false));
            }

            return safeMap;
        } catch (Exception e) {
          return new ArrayList<>();
        }
    }

    private List<String> convertReportCsv(File reportFile) {
        try {
            String csvData = FileUtils.readFile(reportFile).toString();
            List<String> lines = Arrays.stream(csvData.split("\n")).toList();
            int idIndex = findIndexOfColumn(lines, "ID");
            List<String> result = lines.stream()
                                         .skip(1)
                                         .map(line -> Arrays.asList(line.split(",")).get(idIndex))
                                         .toList();

            return result;
        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    private static int findIndexOfColumn(List<String> lines, String columnName) {
        for (String line : lines) {
            List<String> columns = Arrays.asList(line.split(","));
            int columnIndex = columns.indexOf(columnName);
            if (columnIndex != -1) {
                return columnIndex;
            }
        }
        throw new IllegalArgumentException("Column not found: " + columnName);
    }

}
