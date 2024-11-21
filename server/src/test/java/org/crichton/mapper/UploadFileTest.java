package org.crichton.mapper;

import org.crichton.configuration.CrichtonDataStorageProperties;
import org.crichton.domain.utils.mapper.ProjectInformationMapper;
import org.crichton.util.constants.DirectoryName;
import org.crichton.util.constants.FileName;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@EnableConfigurationProperties(CrichtonDataStorageProperties.class)
@TestPropertySource(locations = "classpath:application.properties")
public class UploadFileTest {

    @Autowired
    private ProjectInformationMapper projectInformationMapper;

    @MockBean
    private CrichtonDataStorageProperties crichtonDataStorageProperties;

    // 테스트 디렉토리 경로
    private Path testDirectory;

//    @Test
    void processAndSplitTestSpecFiles_shouldProcessResourceFile() throws Exception {
        // Arrange
        String basePath = "crichton/data";
        testDirectory = Path.of(basePath, "test");
        Files.createDirectories(testDirectory.resolve(DirectoryName.INJECT_TEST));
        copyResourceFileToTestDirectory("testSpecFile.json", testDirectory.resolve(DirectoryName.INJECT_TEST).resolve(FileName.TEST_SPEC).toString());

        // Use Reflection to access private method
        Method method = ProjectInformationMapper.class.getDeclaredMethod("processAndSplitTestSpecFiles", Path.class);
        method.setAccessible(true);

        // Act
        method.invoke(projectInformationMapper, testDirectory);

        // Assert
        File defectFile = testDirectory.resolve(DirectoryName.INJECT_TEST).resolve(FileName.DEFECT_SPEC).toFile();
        assertThat(defectFile).exists();


        method = ProjectInformationMapper.class.getDeclaredMethod("replaceTestSpecTaskFilePath", Path.class);
        method.setAccessible(true);

        // Act
        method.invoke(projectInformationMapper, testDirectory.toAbsolutePath());

        // Optionally check file content
        // assertThat(defectFile).hasContent("{expected_json_content}");
    }

    private void copyResourceFileToTestDirectory(String resourceName, String targetPath) throws Exception {
        try (InputStream resourceStream = getClass().getResourceAsStream("/" + resourceName)) {
            if (resourceStream == null) {
                throw new IllegalArgumentException("Resource not found: " + resourceName);
            }
            Files.copy(resourceStream, Path.of(targetPath), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @AfterEach
    void cleanUp() throws Exception {
        if (testDirectory != null && Files.exists(testDirectory)) {
            Files.walk(testDirectory)
                    .map(Path::toFile)
                    .forEach(File::delete);

            Files.deleteIfExists(testDirectory);
        }
    }
}
