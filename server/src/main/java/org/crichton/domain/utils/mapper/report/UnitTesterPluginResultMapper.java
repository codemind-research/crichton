package org.crichton.domain.utils.mapper.report;

import org.crichton.models.defect.UnitTestDefectInfo;
import org.crichton.models.report.UnitTestPluginReport;
import org.crichton.models.report.UnitTestReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import runner.dto.ProcessedReportDTO;

import java.util.*;
import java.util.stream.Collectors;


@Mapper
public interface UnitTesterPluginResultMapper {

    Logger log = LoggerFactory.getLogger(UnitTesterPluginResultMapper.class);

    // Add a static method to get the mapper instance
    UnitTesterPluginResultMapper INSTANCE = Mappers.getMapper(UnitTesterPluginResultMapper.class);

    @Mapping(target = "pluginName", source = "pluginName")
    @Mapping(target = "reports", source = "info", qualifiedByName = "toUnitTestReports")
    UnitTestPluginReport toUnitTestPluginReport(ProcessedReportDTO report);

    @SuppressWarnings("unchecked")
    @Named("toUnitTestReports")
    default List<UnitTestReport> toUnitTestReports(Map<String, Object> info) {

        return Optional.ofNullable(info.get(UnitTestReport.Key.FILE))
                .filter(List.class::isInstance)
                .map(files -> {

                    var x = ((List<Map<String, Object>>) files).stream()
                            .filter(fileInfo -> fileInfo.containsKey(UnitTestReport.Key.UNIT) && (fileInfo.get(UnitTestReport.Key.UNIT) instanceof List<?>))
                            .map(this::toUnitTestReport)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toUnmodifiableList());

                            return x;

                })
                .orElse(Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    default UnitTestReport toUnitTestReport(Map<String, Object> fileInfo) {
        try {
            var defectInfos = ((List<Map<String, Object>>) fileInfo.get(UnitTestReport.Key.UNIT)).stream()
                    .map(this::toUnitTestDefectInfo)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableList());

            String filePath = ((String) fileInfo.get(UnitTestReport.Key.TITLE)).replaceFirst("^src/", "");

            return UnitTestReport.builder()
                    .file(filePath)
                    .defectInfos(defectInfos)
                    .build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            log.warn("skip mapping. next mapping try....");
            return null;
        }
    }

    default UnitTestDefectInfo toUnitTestDefectInfo(Map<String, Object> unitInfo) {
        try {
            return UnitTestDefectInfo.builder()
                    .functionName(((String) unitInfo.get(UnitTestReport.Key.FUNCTION_NAME)).replaceAll("^[^\\s]+\\s+([^\\(]+)\\s*\\(.*", "$1"))
                    .divisionByZeroCount(parseIntOrDefault(unitInfo, UnitTestReport.Key.DIV_ZERO, 0))
                    .nullAccessCount(parseIntOrDefault(unitInfo, UnitTestReport.Key.NULL_ACCESS, 0))
                    .segFaultsCount(parseIntOrDefault(unitInfo, UnitTestReport.Key.SEGFAULTS, 0))
                    .arrayOutOfBoundCount(parseIntOrDefault(unitInfo, UnitTestReport.Key.ARRAY_OUT_OF_BOUND, 0))
                    .assertsCount(parseIntOrDefault(unitInfo, UnitTestReport.Key.ASSERTS, 0))
                    .timeoutsCount(parseIntOrDefault(unitInfo, UnitTestReport.Key.TIMEOUTS, 0))
                    .failureFactorsCount(parseIntOrDefault(unitInfo, UnitTestReport.Key.FAILURE_FACTORS, 0))
                    .build();
        } catch (NullPointerException e) {
            log.warn("Null pointer exception while mapping UnitInfo to DefectInfo", e);
            return null;
        }
    }


    default Integer parseIntOrDefault(Map<String, Object> map, String key, Integer defaultValue) {
        // key가 존재하고 값이 null이 아닌 경우만 파싱
        return Optional.ofNullable(map.containsKey(key) ? map.get(key) : null)
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }


}
