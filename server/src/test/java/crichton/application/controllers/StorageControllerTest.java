package crichton.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.crichton.application.exceptions.handler.GlobalExceptionResponse;
import org.crichton.domain.services.AccessTokenService;
import org.crichton.domain.services.RefreshTokenService;
import org.crichton.paths.DirectoryPaths;
import org.crichton.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private AccessTokenService accessTokenService;

    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void sourcePathInit() {
        String userId = UUID.randomUUID().toString();
        accessToken = accessTokenService.generateAccessToken(userId);
        refreshToken = refreshTokenService.generateRefreshToken(userId);
    }

    private void deleteUploadSource(File source, File zip){
        if (source.exists()){
            FileUtils.removeDirectory(source);
        }
        if (zip.exists()){
            zip.delete();
        }

    }

    @AfterEach
    public void delete() {
        File uploadSource =  DirectoryPaths.generateUnzipPath("uploadTest").toFile();
        File uploadSourceZiP =  DirectoryPaths.generateZipPath("uploadTest").toFile();
        deleteUploadSource(uploadSource,uploadSourceZiP);
        File uploadFailedSource =  DirectoryPaths.generateUnzipPath("FailedTest").toFile();
        File uploadFailedSourceZiP =  DirectoryPaths.generateZipPath("FailedTest").toFile();
        deleteUploadSource(uploadFailedSource,uploadFailedSourceZiP);
    }

    private Path createZipFile(String zipFileName, byte[] content) throws IOException {
        Path zipFilePath = Files.createTempFile(zipFileName, "");
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFilePath, StandardOpenOption.WRITE))) {
            ZipEntry entry = new ZipEntry("uploadTest.c");
            zipOutputStream.putNextEntry(entry);
            zipOutputStream.write(content);
            zipOutputStream.closeEntry();
        }
        return zipFilePath;
    }

    @Test
    void uploadFile() throws Exception{

        Path zipFilePath = createZipFile("uploadTest", "int main() {return 0;}".getBytes());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "uploadTest.zip",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                Files.newInputStream(zipFilePath)
        );
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/crichton/storage/upload").file(file)
                                              .header("Authorization", accessToken)
                                              .header("RefreshToken", refreshToken))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void uploadFailed() throws Exception{

        MockMultipartFile file = new MockMultipartFile(
                "file",                      // parameter
                "FailedTest",                  // file name
                MediaType.TEXT_PLAIN_VALUE,  // file type
                "Hello, World!".getBytes()   // content
        );

        String result =  mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/crichton/storage/upload").file(file)
                                                               .header("Authorization", accessToken)
                                                               .header("RefreshToken", refreshToken))
               .andExpect(MockMvcResultMatchers.status().is5xxServerError())
               .andDo(MockMvcResultHandlers.print())
               .andReturn()
               .getResponse()
               .getContentAsString()
               .replaceAll("^\"|\"$", "");

        GlobalExceptionResponse response = mapper.readValue(result ,GlobalExceptionResponse.class);
        assertEquals("F002", response.getCode());
    }


}