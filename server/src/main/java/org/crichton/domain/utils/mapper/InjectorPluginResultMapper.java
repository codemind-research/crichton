package org.crichton.domain.utils.mapper;

import org.crichton.domain.dtos.report.InjectionTestDefectDto;
import org.crichton.models.report.DefectReport;
import org.crichton.models.report.InjectorPluginReport;
import org.crichton.models.safe.SafeSpec;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import runner.dto.ProcessedReportDTO;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper
public interface InjectorPluginResultMapper {

    // Add a static method to get the mapper instance
    InjectorPluginResultMapper INSTANCE = Mappers.getMapper(InjectorPluginResultMapper.class);

    @Mapping(target = "defectId", source = "key")
    @Mapping(target = "file", source = "value.target", qualifiedByName = "mapTargetToString")
    @Mapping(target = "safeSpecs", source = "value.safe", qualifiedByName = "convertSafeSpecList")
    DefectReport mapEntryToDefectReport(Map.Entry<String, Map<String, Object>> entry);

    @Named("mapTargetToString")
    default String mapTargetToString(Object target) {
        return target != null ? target.toString() : null;
    }

    @Named("convertSafeSpecList")
    default List<SafeSpec> convertSafeSpecs(Object safesObj) {
        if (safesObj instanceof List) {
            List<Map<String, Object>> safes = (List<Map<String, Object>>) safesObj;
            return safes.stream()
                    .map(safe -> SafeSpec.builder()
                            .id(Integer.parseInt(String.valueOf(safe.get("id"))))
                            .build())
                    .collect(Collectors.toUnmodifiableList());
        }
        return Collections.emptyList();
    }

    default List<SafeSpec> convertSafeSpecs(List<Map<String, Object>> safes) {
        return safes.stream()
                .map(safe -> SafeSpec.builder()
                        .id(Integer.parseInt(String.valueOf(safe.get("id"))))
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }

    @Mapping(target = "pluginName", source = "pluginName")
    @Mapping(target = "defectReports", expression = "java(mapInfoToDefectReports(report.getInfo()))")
    InjectorPluginReport processedReportDtoToInjectorPluginReport(ProcessedReportDTO report);

    default List<DefectReport> mapInfoToDefectReports(LinkedHashMap<String, Object> info) {
        return info.entrySet().stream()
                .map(entry -> {
                    var defectId = Integer.parseInt(entry.getKey());
                    var value = (Map<String, Object>) entry.getValue();
                    var file = String.valueOf(value.get("target"));
                    List<Map<String, Object>> safes = (List<Map<String, Object>>) value.get("safe");
                    List<SafeSpec> safeSpecs = safes.stream()
                            .map(safe -> SafeSpec.builder().id(Integer.parseInt(String.valueOf(safe.get("id")))).build())
                            .collect(Collectors.toUnmodifiableList());
                    return DefectReport.builder()
                            .defectId(defectId)
                            .file(file)
                            .safeSpecs(safeSpecs)
                            .build();
                })
                .collect(Collectors.toUnmodifiableList());
    }

    // New method for transforming List<DefectReport> into List<InjectionTestDefectDto>
    default List<InjectionTestDefectDto> defectReportsToInjectionTestDefectDtos(List<DefectReport> defectReports) {
        return defectReports.stream()
                .flatMap(defectReport -> defectReport.safeSpecs().stream()
                        .map(safeSpec -> InjectionTestDefectDto.builder()
                                .defectId(defectReport.defectId())
                                .file(defectReport.file())
                                .violationId(safeSpec.id())
                                .build()))
                .collect(Collectors.toList());
    }

    // Method to convert ProcessedReportDTO to List<InjectionTestDefectDto>
    default List<InjectionTestDefectDto> processedReportDtoToInjectinoTestDefectDtos(ProcessedReportDTO report) {
        List<DefectReport> defectReports = mapInfoToDefectReports(report.getInfo());
        return defectReportsToInjectionTestDefectDtos(defectReports);
    }
}
