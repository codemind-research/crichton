package org.crichton.domain.utils.mapper.report;

import org.crichton.domain.dtos.report.InjectionTestDefectDto;
import org.crichton.models.report.InjectorDefectReport;
import org.crichton.models.report.InjectorPluginReport;
import org.crichton.models.safe.SafeSpec;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import runner.dto.ProcessedReportDTO;

import java.util.*;
import java.util.stream.Collectors;

@Mapper
public interface InjectorPluginResultMapper {

    // Add a static method to get the mapper instance
    InjectorPluginResultMapper INSTANCE = Mappers.getMapper(InjectorPluginResultMapper.class);


    @Mapping(target = "pluginName", source = "pluginName")
    @Mapping(target = "reports", expression = "java(toInjectorDefectReports(report.getInfo()))")
    InjectorPluginReport toInjectorPluginReport(ProcessedReportDTO report);

    @SuppressWarnings("unchecked")
    default List<InjectorDefectReport> toInjectorDefectReports(LinkedHashMap<String, Object> info) {
        return info.entrySet().stream()
                .map(entry -> {
                    var defectId = Integer.parseInt(entry.getKey());
                    var value = (Map<String, Object>) entry.getValue();
                    var file = String.valueOf(value.get("target"));
                    List<Map<String, Object>> safes = (List<Map<String, Object>>) value.get("safe");
                    List<SafeSpec> safeSpecs = safes.stream()
                            .map(safe -> SafeSpec.builder().id(Integer.parseInt(String.valueOf(safe.get("id")))).build())
                            .collect(Collectors.toUnmodifiableList());
                    return InjectorDefectReport.builder()
                            .defectId(defectId)
                            .file(file)
                            .safeSpecs(safeSpecs)
                            .build();
                })
                .collect(Collectors.toUnmodifiableList());
    }

    // New method for transforming List<DefectReport> into List<InjectionTestDefectDto>
    default List<InjectionTestDefectDto> toInjectionTestDefectDtos(List<InjectorDefectReport> injectorDefectReports) {
        return injectorDefectReports.stream()
                .flatMap(injectorDefectReport -> injectorDefectReport.safeSpecs().stream()
                        .map(safeSpec -> InjectionTestDefectDto.builder()
                                .defectId(injectorDefectReport.defectId())
                                .file(injectorDefectReport.file())
                                .violationId(safeSpec.id())
                                .build()))
                .collect(Collectors.toList());
    }

    // Method to convert ProcessedReportDTO to List<InjectionTestDefectDto>
    default List<InjectionTestDefectDto> toInjectionTestDefectDtos(ProcessedReportDTO report) {
        List<InjectorDefectReport> injectorDefectReports = toInjectorDefectReports(report.getInfo());
        return toInjectionTestDefectDtos(injectorDefectReports);
    }
}
