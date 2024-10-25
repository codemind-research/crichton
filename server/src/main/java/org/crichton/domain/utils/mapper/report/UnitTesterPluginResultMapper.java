package org.crichton.domain.utils.mapper.report;

import lombok.extern.slf4j.Slf4j;
import org.crichton.models.defect.UnitTestDefectInfo;
import org.crichton.models.report.UnitTestPluginReport;
import org.crichton.models.report.UnitTestReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import runner.dto.ProcessedReportDTO;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Mapper
public interface UnitTesterPluginResultMapper {

    Logger log = LoggerFactory.getLogger(UnitTesterPluginResultMapper.class);

    // Add a static method to get the mapper instance
    UnitTesterPluginResultMapper INSTANCE = Mappers.getMapper(UnitTesterPluginResultMapper.class);


    @Mapping(target = "pluginName", source = "pluginName")
    @Mapping(target = "reports", expression = "java(toUnitTestReports(report.getInfo()))")
    UnitTestPluginReport toUnitTestPluginReport(ProcessedReportDTO report);


    default List<UnitTestReport> toUnitTestReports(Map<String, Object> info) {
        if(info.containsKey(UnitTestReport.Key.FILE)) {
            var fileInfos = (List<Map<String, Object>>) info.get(UnitTestReport.Key.FILE);
            fileInfos.stream()
                    .filter(fileInfo -> fileInfo.containsKey(UnitTestReport.Key.UNIT) && fileInfo.get(UnitTestReport.Key.UNIT) instanceof List<?>)
                    .map(fileInfo -> {

                        try {
                            List<Map<String, Object>> fileUnitInfos = (List<Map<String, Object>>) fileInfo.get(UnitTestReport.Key.UNIT);
                            var unitTestDefectInfos = fileUnitInfos.stream()
                                    .map(fileUnitInfo -> {

                                        try {

                                            var functionName = (String) fileUnitInfo.get(UnitTestReport.Key.FUNCTION_NAME);
                                            var divisionByZeroCount = (int) fileUnitInfo.getOrDefault(UnitTestReport.Key.DIV_ZERO, 0);
                                            var nullAccessCount = (int) fileUnitInfo.getOrDefault(UnitTestReport.Key.NULL_ACCESS, 0);
                                            var segFaultsCount = (int) fileUnitInfo.getOrDefault(UnitTestReport.Key.SEGFAULTS, 0);
                                            var arrayOutOfBound = (int) fileUnitInfo.getOrDefault(UnitTestReport.Key.ARRAY_OUT_OF_BOUND, 0);
                                            var assertsCount = (int) fileUnitInfo.getOrDefault(UnitTestReport.Key.ASSERTS, 0);
                                            var timeoutsCount = (int) fileUnitInfo.getOrDefault(UnitTestReport.Key.TIMEOUTS, 0);
                                            var failureFactorsCount = (int) fileUnitInfo.getOrDefault(UnitTestReport.Key.FAILURE_FACTORS, 0);

                                            return UnitTestDefectInfo.builder()
                                                    .functionName(functionName)
                                                    .divisionByZeroCount(divisionByZeroCount)
                                                    .nullAccessCount(nullAccessCount)
                                                    .segFaultsCount(segFaultsCount)
                                                    .assertsCount(assertsCount)
                                                    .failureFactorsCount(failureFactorsCount)
                                                    .timeoutsCount(timeoutsCount)
                                                    .assertsCount(arrayOutOfBound)
                                                    .build();
                                        }
                                        catch (NullPointerException e) {
                                            throw e;
                                        }
                                    })
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toUnmodifiableList());

                            var filePath = (String) fileInfo.get(UnitTestReport.Key.FILE);

                            return UnitTestReport.builder()
                                    .file(filePath)
                                    .defectInfos(unitTestDefectInfos);

                        }
                        catch (Exception e) {
                            log.warn(e.getMessage(), e);
                            log.warn("skip mapping. next mapping try....");
                            return null;
                        }

                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableList());

        }

        return List.of();
    }

}
