package org.crichton.domain.utils.mapper;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.crichton.configuration.CrichtonDataStorageProperties;
import org.crichton.domain.dtos.project.CreationProjectInformationDto;
import org.crichton.domain.entities.ProjectInformation;
import org.crichton.util.constants.FileName;
import org.mapstruct.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class})
public abstract class ProjectInformationMapper {

    @Autowired
    private CrichtonDataStorageProperties crichtonDataStorageProperties;


    public ProjectInformation toEntry(CreationProjectInformationDto createdDto) throws IOException {
        createFiles(createdDto);
        return toEntryInternal(createdDto);
    };

    // 매핑 메서드를 별도로 정의하여 `createFiles` 후에 실제 매핑 수행
    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "status", constant = "None")
    @Mapping(target = "testResult", constant = "None")
    @Mapping(target = "failReason", ignore = true)
    protected abstract ProjectInformation toEntryInternal(CreationProjectInformationDto createdDto);


    protected void createFiles(CreationProjectInformationDto dto) throws IOException {
        try {
            var uuid = UUID.randomUUID();

            String baseDirPath = crichtonDataStorageProperties.getBasePath() + File.separator + uuid;

            File baseDir = new File(baseDirPath);

            if(!baseDir.exists()) {
                baseDir.mkdirs();
            }

            // sourceCode 파일 압축 해제
            if (dto.getSourceCode() != null) {
                unzipFile(dto.getSourceCode(), baseDirPath);
            }

            // 나머지 파일 저장
            if (dto.getTestSpecFile() != null) {
                saveFile(dto.getTestSpecFile(), baseDirPath, FileName.TEST_SPEC);
            }
            if (dto.getDefectSpecFile() != null) {
                saveFile(dto.getDefectSpecFile(), baseDirPath, FileName.DEFECT_SPEC);
            }
            if (dto.getSafeSpecFile() != null) {
                saveFile(dto.getSafeSpecFile(), baseDirPath, FileName.SAFE_SPEC);
            }
            if (dto.getUnitTestSpecFile() != null) {
                saveFile(dto.getUnitTestSpecFile(), baseDirPath, FileName.UNIT_TEST_SPEC);
            }

            dto.setUuid(uuid);
        }
        catch(IOException e) {
            throw e;
        }

    }


    // 파일 저장 메서드
    protected String saveFile(MultipartFile file, String dirPath, String fileName) throws IOException {
        Path filePath = Paths.get(dirPath, fileName);
        Files.write(filePath, file.getBytes());
        return filePath.toString();
    }

    // Zip4j를 사용한 압축 해제 메서드
    protected void unzipFile(MultipartFile zipFile, String destDir) throws IOException, ZipException {
        File tempZipFile = Files.createTempFile("temp", ".zip").toFile();
        zipFile.transferTo(tempZipFile);

        try (ZipFile zip = new ZipFile(tempZipFile)) {
            zip.extractAll(destDir);
        } finally {
            tempZipFile.delete();
        }
    }

}
