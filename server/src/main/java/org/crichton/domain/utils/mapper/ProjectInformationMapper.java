package org.crichton.domain.utils.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.crichton.configuration.CrichtonDataStorageProperties;
import org.crichton.domain.dtos.project.CreationProjectInformationDto;
import org.crichton.domain.dtos.spec.TestSpecDto;
import org.crichton.domain.entities.ProjectInformation;
import org.crichton.util.FileUtils;
import org.crichton.util.ObjectMapperUtils;
import org.crichton.util.OperationSystemUtil;
import org.crichton.util.constants.DirectoryName;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mapper(componentModel = "spring", imports = {UUID.class}, uses = { TestSpecMapper.class })
public abstract class ProjectInformationMapper {

    private static final Logger log = LoggerFactory.getLogger(ProjectInformationMapper.class);

    @Autowired
    private CrichtonDataStorageProperties crichtonDataStorageProperties;

    @Autowired
    private OperationSystemUtil operationSystemUtil;


    public ProjectInformation toEntry(CreationProjectInformationDto createdDto) throws IOException, NoSuchFieldException {
        createFiles(createdDto);
        var entity = toEntryInternal(createdDto);

        var baseDirAbsolutePath = Paths.get(crichtonDataStorageProperties.getBasePath(), entity.getId().toString()).toAbsolutePath();
        try {
            processAndSplitTestSpecFiles(baseDirAbsolutePath);
            replaceTestSpecTaskFilePath(baseDirAbsolutePath);
        }
        catch (Exception e) {
            if(baseDirAbsolutePath.toFile().exists()) {
                try {
                    FileUtils.deleteDirectoryRecursively(baseDirAbsolutePath);
                } catch (IOException e1) {
                    throw new IOException(e1);
                }
            }
            throw e;
        }
        return entity;
    };

    // 매핑 메서드를 별도로 정의하여 `createFiles` 후에 실제 매핑 수행
    @Mapping(source = "uuid", target = "id")
    @Mapping(target = "status", constant = "None")
    @Mapping(target = "testResult", constant = "None")
    @Mapping(target = "failReason", ignore = true)
    @Mapping(target = "pluginProcessorId", ignore = true)
    @Mapping(target = "injectorPluginReport", ignore = true)
    @Mapping(target = "unitTestPluginReport", ignore = true)
    protected abstract ProjectInformation toEntryInternal(CreationProjectInformationDto createdDto);


    private void createFiles(CreationProjectInformationDto dto) throws IOException {
        try {
            var uuid = UUID.randomUUID();

            log.info("Create Project File: {}", uuid);
            String workingDirectory = FileUtils.getAbsolutePath(crichtonDataStorageProperties.getBasePath(), uuid.toString());

            String sourceDirectoryPath = FileUtils.getAbsolutePath(workingDirectory, DirectoryName.SOURCE);

            log.info("Make project source directory: {}", sourceDirectoryPath);
            FileUtils.makeDirectory(sourceDirectoryPath);
            dto.setSourceDirectoryPath(sourceDirectoryPath);

            var defectDirectoryPath = FileUtils.getAbsolutePath(workingDirectory, DirectoryName.INJECT_TEST);

            log.info("Make project defect directory: {}", defectDirectoryPath);
            FileUtils.makeDirectory(defectDirectoryPath);
            dto.setSourceDirectoryPath(defectDirectoryPath);

            var unitTestDirectoryPath = FileUtils.getAbsolutePath(workingDirectory, DirectoryName.UNIT_TEST);

            log.info("Make project unit test directory: {}", unitTestDirectoryPath);
            FileUtils.makeDirectory(unitTestDirectoryPath);
            dto.setSourceDirectoryPath(unitTestDirectoryPath);

            // sourceCode 파일 압축 해제
            if (dto.getSourceCode() != null) {
                log.info("unzip source file: {}", dto.getSourceCode());
                FileUtils.CompressFile.extractFile(dto.getSourceCode(), sourceDirectoryPath);
            }

            // 나머지 파일 저장
            if (dto.getTestSpecFile() != null) {

                var testSpecFilePath = FileUtils.getFilePath(defectDirectoryPath, FileName.TEST_SPEC);

                log.info("save test spec file: {}", testSpecFilePath);
                saveFile(dto.getTestSpecFile(), testSpecFilePath);
            }

            if (dto.getSafeSpecFile() != null) {
                var safeSpecFilePath = FileUtils.getFilePath(defectDirectoryPath, FileName.SAFE_SPEC);

                log.info("save safe spec file: {}", safeSpecFilePath);
                saveFile(dto.getSafeSpecFile(), safeSpecFilePath);
            }

            if (dto.getUnitTestSpecFile() != null) {
                var unitTestSpecFilePath = FileUtils.getFilePath(unitTestDirectoryPath, FileName.UNIT_TEST_PROJECT_SETTINGS);

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

    @SuppressWarnings("unchecked")
    private void processAndSplitTestSpecFiles(Path baseDirectory) throws IOException, NoSuchFieldException {

        Path testSpecFilePath = baseDirectory.resolve(DirectoryName.INJECT_TEST).resolve(FileName.TEST_SPEC);
        var jsonNode = ObjectMapperUtils.getJsonNode(testSpecFilePath.toFile());

        Path injectSpecFilesStoreDirectoryPath = baseDirectory.resolve(DirectoryName.INJECT_TEST);
        splitDefectsToFile(jsonNode, injectSpecFilesStoreDirectoryPath.resolve(FileName.DEFECT_SPEC));
        splitBuildsToFiles(jsonNode, injectSpecFilesStoreDirectoryPath);
    }

    private void splitDefectsToFile(JsonNode jsonNode, Path defectSpecFilePath) throws NoSuchFieldException {
        if (jsonNode.has("defects")) {
            log.info("Saving defects to file: {}", defectSpecFilePath);
            ObjectMapperUtils.saveObjectToJsonFile(jsonNode.get("defects"), defectSpecFilePath.toFile());
        } else {
            throw new NoSuchFieldException("Key 'defects' not found in the test specification JSON.");
        }
    }

    @SuppressWarnings("unchecked")
    private void splitBuildsToFiles(JsonNode jsonNode, Path injectorDirectoryPath) throws NoSuchFieldException {
        if (jsonNode.has("builds")) {
            Map<String, Object> builds = ObjectMapperUtils.convertValue(jsonNode.get("builds"), Map.class);
            for (var entry : builds.entrySet()) {
                String buildSpecFileName = entry.getKey() + ".json";
                Path buildSpecFilePath = injectorDirectoryPath.resolve(buildSpecFileName);
                log.info("Saving build '{}' to file: {}", entry.getKey(), buildSpecFilePath);
                ObjectMapperUtils.saveObjectToJsonFile(entry.getValue(), buildSpecFilePath.toFile());
            }
        } else {
            throw new NoSuchFieldException("Key 'builds' not found in the test specification JSON.");
        }
    }

    protected void replaceTestSpecTaskFilePath(final Path baseDirAbsolutePath) {

        try {

            final Path sourceDirAbsolutePath = baseDirAbsolutePath.resolve(DirectoryName.SOURCE);

            final Path injectTesterDirectoryPath = baseDirAbsolutePath.resolve(DirectoryName.INJECT_TEST).toAbsolutePath();
            Path defectSpecFilePath = baseDirAbsolutePath.resolve(DirectoryName.INJECT_TEST).resolve(FileName.DEFECT_SPEC);

            log.debug("Overwrite the modified values into file '{}'.", defectSpecFilePath.toAbsolutePath());
            ObjectMapperUtils.modifyJsonFile(defectSpecFilePath, "target", (value) ->  convertToLocalPath(sourceDirAbsolutePath, value), String.class);

            Set<String> buildSpecFilePaths = ConcurrentHashMap.newKeySet();
            ObjectMapperUtils.modifyJsonFile(defectSpecFilePath, "build", (value) ->  {
                String buildSpecFilePath = convertToLocalPath(injectTesterDirectoryPath, String.format("%s.json", value));
                buildSpecFilePaths.add(buildSpecFilePath);

                return buildSpecFilePath;
            }, String.class);

            for(var buildSpecFilePath : buildSpecFilePaths) {
                log.debug("Overwrite the modified values into file '{}'.", buildSpecFilePath);
                ObjectMapperUtils.modifyJsonFile(buildSpecFilePath, "tasks.file", (fileName) ->  convertToLocalPath(sourceDirAbsolutePath, fileName), String.class);
                ObjectMapperUtils.modifyJsonFile(buildSpecFilePath, "extra_srcs", (fileName) ->  convertToLocalPath(sourceDirAbsolutePath, fileName), String.class);
            }


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
