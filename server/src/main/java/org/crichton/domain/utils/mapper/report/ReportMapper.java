package org.crichton.domain.utils.mapper.report;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.crichton.configuration.CrichtonDataStorageProperties;
import org.crichton.domain.dtos.report.InjectionTestDefectDto;
import org.crichton.domain.dtos.report.ResponseReportDto;
import org.crichton.domain.dtos.report.UnitTestDefectDto;
import org.crichton.domain.entities.ProjectInformation;
import org.crichton.models.defect.UnitTestDefectInfo;
import org.crichton.models.report.InjectorDefectReport;
import org.crichton.models.report.InjectorPluginReport;
import org.crichton.models.report.UnitTestPluginReport;
import org.crichton.models.report.UnitTestReport;
import org.crichton.models.safe.SafeSpec;
import org.crichton.util.FileUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Mapper(componentModel = "spring", imports = {UUID.class})
public abstract class ReportMapper {

    @Autowired
    private CrichtonDataStorageProperties dataStorageProperties;


    @Mapping(target = "injectionTestDefects", expression = "java(toInjectionTestDefects(projectInformation.getId(), projectInformation.getInjectorPluginReport()))")
    @Mapping(target = "unitTestDefects", expression = "java(toUnitTestDefectDtos(projectInformation.getUnitTestPluginReport()))")
    public abstract ResponseReportDto toResponseReportDto(@NonNull ProjectInformation projectInformation);

    @Named("toInjectionTestDefects")
    public List<InjectionTestDefectDto> toInjectionTestDefects(@NonNull UUID projectInformationId, InjectorPluginReport injectorPluginReport) {
        if (injectorPluginReport != null) {
            return toInjectionTestDefects(projectInformationId, injectorPluginReport.getReports());
        } else {
            return List.of();
        }
    }

    @Named("toInjectionTestDefects")
    public List<InjectionTestDefectDto> toInjectionTestDefects(@NonNull UUID projectInformationId, @NonNull List<InjectorDefectReport> injectorDefectReports) {

        String projectDirectoryPath = FileUtils.getAbsolutePath(dataStorageProperties.getBasePath(), projectInformationId.toString());

        List<InjectionTestDefectDto> dtos = new ArrayList<>();
        for (InjectorDefectReport injectorDefectReport : injectorDefectReports) {
            String file = injectorDefectReport.file().replace(projectDirectoryPath, StringUtils.EMPTY).substring(1);

            List<SafeSpec> safeSpecs = injectorDefectReport.safeSpecs().stream()
                    .filter(safeSpec -> !safeSpec.isSuccess())
                    .collect(Collectors.toUnmodifiableList());

            for (SafeSpec safeSpec : safeSpecs) {
                var dto = InjectionTestDefectDto.builder()
                        .file(file)
                        .defectId(injectorDefectReport.defectId())
                        .violationId(safeSpec.id())
                        .build();
                dtos.add(dto);
            }
        }

        return dtos;
    }

    @Named("toUnitTestDefectDtos")
    public List<UnitTestDefectDto> toUnitTestDefectDtos(UnitTestPluginReport unitTestPluginReport) {
        return unitTestPluginReport.getReports().stream()
                .flatMap(report -> report.defectInfos().stream()
                        .filter(UnitTestDefectInfo::isDefectFound)
                        .map(defectInfo -> toUnitTestDefectDto(
                                report.file(),
                                defectInfo.getFunctionName(),
                                defectInfo.getDetectedDefectCodes())))
                .collect(Collectors.toList());
    }

    @Mapping(target = "file", source = "file")
    @Mapping(target = "functionName", source = "functionName")
    @Mapping(target = "defectCode", source = "detectedDefectCodes")
    public abstract UnitTestDefectDto toUnitTestDefectDto(String file, String functionName, String detectedDefectCodes);


}
