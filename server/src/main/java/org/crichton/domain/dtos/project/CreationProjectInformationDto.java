package org.crichton.domain.dtos.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.crichton.domain.dtos.spec.TestSpecDto;
import org.crichton.domain.utils.anotations.ValidFile;
import org.crichton.models.defect.DefectSpec;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.crichton.domain.utils.enums.UploadAllowFileDefine.*;

@Getter
public class CreationProjectInformationDto {

    @Setter
    @JsonIgnore
    private UUID uuid;

    @Setter
    @ValidFile(allowFileDefines = {ZIP, ZIP7, TAR, GZ, TGZ, TAR_GZ, GTAR, XZ, BZ2, RAR}, message = "Only ZIP files are allowed for sourceCode")
    private MultipartFile sourceCode;

    @Setter
    @ValidFile(allowFileDefines = { JSON }, message = "Only JSON files are allowed for testSpecFile")
    private MultipartFile testSpecFile;

    @Setter
    @ValidFile(allowFileDefines = { JSON }, message = "Only JSON files are allowed for defectSpecFile")
    private MultipartFile defectSpecFile;

    @Setter
    @ValidFile(allowFileDefines = { JSON}, message = "Only JSON files are allowed for safeSpecFile")
    private MultipartFile safeSpecFile;

    @Setter
    @ValidFile(allowFileDefines = { JSON }, required = false, message = "Only JSON files are allowed for unitTestSpecFile")
    private MultipartFile unitTestSpecFile;

    @Setter
    @JsonIgnore
    private TestSpecDto testSpec = TestSpecDto.builder().build();

    @Setter
    @JsonIgnore
    private List<DefectSpec> defectSpecs = new ArrayList<>();

    @Setter
    @JsonIgnore
    private String sourceDirectoryPath;

    @Setter
    @JsonIgnore
    private String injectTestDirectoryPath;

    @Setter
    @JsonIgnore
    private String unitTestDirectoryPath;
}
