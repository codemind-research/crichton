package injector.report;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import injector.setting.DefectInjectorSetting;
import runner.util.FileUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Parser {

    private final DefectInjectorSetting setting;

    public Parser(DefectInjectorSetting setting) {
        this.setting = setting;
    }

    public LinkedHashMap<String, Object> convert() {
        File defectJson = setting.getDefectJson();
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            LinkedHashMap<String, Object> defectMap = objectMapper
                    .readValue(defectJson, new TypeReference<List<LinkedHashMap<String, Object>>>() {})
                    .stream()
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
            File safeJson = setting.getSafeJson();
            File report = setting.getOutputFile(id);
            ObjectMapper objectMapper = new ObjectMapper();
            List<LinkedHashMap<String, Object>> safeMap = objectMapper
                    .readValue(safeJson, new TypeReference<List<LinkedHashMap<String, Object>>>() {});
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
