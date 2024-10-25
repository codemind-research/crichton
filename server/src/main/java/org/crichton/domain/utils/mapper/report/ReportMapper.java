package org.crichton.domain.utils.mapper.report;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.crichton.configuration.CrichtonDataStorageProperties;
import org.crichton.domain.dtos.report.InjectionTestDefectDto;
import org.crichton.domain.dtos.report.ResponseReportDto;
import org.crichton.domain.dtos.report.UnitTestDefectDto;
import org.crichton.domain.entities.ProjectInformation;
import org.crichton.models.report.InjectorDefectReport;
import org.crichton.models.report.InjectorPluginReport;
import org.crichton.models.report.UnitTestPluginReport;
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
    @Mapping(target = "unitTestDefects", expression = "java(toUnitTestDefectDtos(projectInformation.getId(), projectInformation.getUnitTestPluginReport()))")
    public abstract ResponseReportDto toResponseReportDto(@NonNull ProjectInformation projectInformation);

    @Named("toInjectionTestDefects")
    public List<InjectionTestDefectDto> toInjectionTestDefects(@NonNull UUID projectInformationId, InjectorPluginReport injectorPluginReport) {
        if(injectorPluginReport != null) {
            return toInjectionTestDefects(projectInformationId, injectorPluginReport.getReports());
        }
        else {
            return new ArrayList<>();
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

    @Named("mapToUnitTestDefectDtos")
    public List<UnitTestDefectDto> toUnitTestDefectDtos(@NonNull UUID projectInformationId, UnitTestPluginReport unitTestPluginReport) {
        return List.of();
    }

}
