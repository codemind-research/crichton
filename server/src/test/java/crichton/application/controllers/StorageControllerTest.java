package crichton.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import crichton.application.exceptions.handler.GlobalExceptionResponse;
import crichton.paths.DirectoryPaths;
import crichton.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    public void deleteUploadSource() {
        File uploadSource =  DirectoryPaths.generateUnzipPath("uploadTest").toFile();
        File uploadSourceZiP =  DirectoryPaths.generateZipPath("uploadTest").toFile();
        if (uploadSource.exists()){
            FileUtils.removeDirectory(uploadSource);
        }
        if (uploadSourceZiP.exists()){
            uploadSourceZiP.delete();
        }
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
                "uploadTest",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                Files.newInputStream(zipFilePath)
        );
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/crichton/storage/upload").file(file))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void uploadFailed() throws Exception{

        MockMultipartFile file = new MockMultipartFile(
                "file",                      // 파라미터 이름
                "test.txt",                  // 파일 이름
                MediaType.TEXT_PLAIN_VALUE,  // 파일 타입
                "Hello, World!".getBytes()   // 파일 내용
        );

        String result =  mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/crichton/storage/upload").file(file))
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