package org.crichton.domain.utils.mapper;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.crichton.configuration.CrichtonDataStorageProperties;
import org.crichton.domain.dtos.project.CreationProjectInformationDto;
import org.crichton.domain.dtos.spec.TestSpecDto;
import org.crichton.domain.entities.ProjectInformation;
import org.crichton.util.FileUtils;
import org.crichton.util.ObjectMapperUtils;
import org.crichton.util.OperationSystemUtil;
import org.crichton.util.constants.FileName;
import org.crichton.util.constants.PluginSettingKey;
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

@Mapper(componentModel = "spring", imports = {UUID.class}, uses = { TestSpecMapper.class })
public abstract class ProjectInformationMapper {

    private static final Logger log = LoggerFactory.getLogger(ProjectInformationMapper.class);

    @Autowired
    private CrichtonDataStorageProperties crichtonDataStorageProperties;

    @Autowired
    private OperationSystemUtil operationSystemUtil;


    public ProjectInformation toEntry(CreationProjectInformationDto createdDto) throws IOException {
        createFiles(createdDto);
        var entity = toEntryInternal(createdDto);
        replaceTestSpecTaskFilePath(entity);
        return entity;
    };

    // 매핑 메서드를 별도로 정의하여 `createFiles` 후에 실제 매핑 수행
    @Mapping(source = "uuid", target = "id")
    @Mapping(target = "status", constant = "None")
    @Mapping(target = "testResult", constant = "None")
    @Mapping(target = "failReason", ignore = true)
    protected abstract ProjectInformation toEntryInternal(CreationProjectInformationDto createdDto);


    private void createFiles(CreationProjectInformationDto dto) throws IOException {
        try {
            var uuid = UUID.randomUUID();

            log.info("Create Project File: {}", uuid);

            String baseDirPath = crichtonDataStorageProperties.getBasePath() + File.separator + uuid;

            log.info("Make project directory: {}", baseDirPath);
            FileUtils.makeDirectory(baseDirPath);

            var defectDirectoryPath = FileUtils.getAbsolutePath(baseDirPath, PluginSettingKey.DefectInjector.DEFECT_SPEC_FILE_PATH);

            log.info("Make project defect directory: {}", defectDirectoryPath);
            FileUtils.makeDirectory(defectDirectoryPath);

            var unitTestDirectoryPath = FileUtils.getAbsolutePath(baseDirPath, PluginSettingKey.UnitTester.UNIT_TEST_SPEC_FILE_PATH);

            log.info("Make project unit test directory: {}", unitTestDirectoryPath);
            FileUtils.makeDirectory(unitTestDirectoryPath);

            // sourceCode 파일 압축 해제
            if (dto.getSourceCode() != null) {
                log.info("unzip source file: {}", dto.getSourceCode());
                unzipFile(dto.getSourceCode(), baseDirPath);
            }

            // 나머지 파일 저장
            if (dto.getTestSpecFile() != null) {

                var testSpecFilePath = FileUtils.getFilePath(defectDirectoryPath, FileName.TEST_SPEC);

                log.info("save test spec file: {}", testSpecFilePath);
                saveFile(dto.getTestSpecFile(), testSpecFilePath);

                log.info("Updated JSON file content: {}", testSpecFilePath);
                var jsonString = Files.readString(testSpecFilePath);
                var testSpecDto = ObjectMapperUtils.convertJsonStringToObject(jsonString, TestSpecDto.class);
                dto.setTestSpec(testSpecDto);
            }

            if (dto.getDefectSpecFile() != null) {
                var defectSpecFilePath = FileUtils.getFilePath(defectDirectoryPath,  FileName.DEFECT_SPEC);

                log.info("save defect spec file: {}", defectSpecFilePath);
                saveFile(dto.getDefectSpecFile(), defectSpecFilePath);
            }

            if (dto.getSafeSpecFile() != null) {
                var safeSpecFilePath = FileUtils.getFilePath(defectDirectoryPath, FileName.SAFE_SPEC);

                log.info("save safe spec file: {}", safeSpecFilePath);
                saveFile(dto.getSafeSpecFile(), safeSpecFilePath);
            }

            if (dto.getUnitTestSpecFile() != null) {
                var unitTestSpecFilePath = FileUtils.getFilePath(unitTestDirectoryPath, FileName.UNIT_TEST_SPEC);

                log.info("save unit test spec file: {}", unitTestSpecFilePath);
                saveFile(dto.getUnitTestSpecFile(), unitTestSpecFilePath);
            }

            dto.setUuid(uuid);
        }
        catch(IOException e) {
            throw e;
        }

    }

    // 파일 저장 메서드
    private String saveFile(MultipartFile file, Path filePath) throws IOException {
        Files.write(filePath, file.getBytes());
        return filePath.toString();
    }

    // Zip4j를 사용한 압축 해제 메서드
    private void unzipFile(MultipartFile zipFile, String destDir) throws IOException, ZipException {
        File tempZipFile = Files.createTempFile("temp", ".zip").toFile();
        zipFile.transferTo(tempZipFile);

        try (ZipFile zip = new ZipFile(tempZipFile)) {
            zip.extractAll(destDir);
        } finally {
            tempZipFile.delete();
        }
    }

    protected void replaceTestSpecTaskFilePath(ProjectInformation target) {

        var baseDirAbsolutePath = Paths.get(crichtonDataStorageProperties.getBasePath(), target.getId().toString()).toAbsolutePath();
        Path testSpecFilePath = FileUtils.getFilePath(baseDirAbsolutePath.toString(), FileName.TEST_SPEC);

        try {
            var jsonString = Files.readString(testSpecFilePath);
            var testSpecDto = ObjectMapperUtils.convertJsonStringToObject(jsonString, TestSpecDto.class);

            for(var taskDto : testSpecDto.getTasks()) {

                var taskLocalFilePath = convertToLocalPath(baseDirAbsolutePath, taskDto.getFile());
                taskDto.setFile(taskLocalFilePath);
            }

            String updatedJsonString = ObjectMapperUtils.convertObjectToJsonString(testSpecDto);

            // JSON 파일 덮어쓰기
            Files.writeString(testSpecFilePath, updatedJsonString);

            log.debug("Updated JSON file content: {}", updatedJsonString);

        }
        catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
        catch (Exception e) {

        }

    }


    private String convertToLocalPath(Path baseDirAbsolutePath, String clientFilePath) {
        // 클라이언트 경로에서 파일 이름까지 포함한 전체 구조 가져오기
        Path clientPath = Paths.get(clientFilePath);

        // 압축 해제 위치를 기준으로 경로 생성
        Path localPath = Paths.get(baseDirAbsolutePath.toString(), clientPath.subpath(0, clientPath.getNameCount()).toString()).normalize();

        if(!operationSystemUtil.isWindows()) {
            return localPath.toString().replace("\\", "/"); // 윈도우 경로 구분자 제거
        }
        else {
            return localPath.toString();
        }


    }
}
