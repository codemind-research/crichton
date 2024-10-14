package org.crichton.domain.dtos.project;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class CreationProjectInformationDto {
    private MultipartFile sourceCode;
    private MultipartFile testSpecFile;
    private MultipartFile defectSpecFile;
    private MultipartFile safeSpecFile;
    private MultipartFile unitTestSpecFile;
}
