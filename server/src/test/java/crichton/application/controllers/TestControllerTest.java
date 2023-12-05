package crichton.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import crichton.application.exceptions.handler.GlobalExceptionResponse;
import crichton.domain.dtos.LogDTO;
import crichton.domain.dtos.ReportDTO;
import crichton.domain.dtos.TestDTO;
import crichton.domain.services.AccessTokenService;
import crichton.domain.services.RefreshTokenService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AccessTokenService accessTokenService;


    private String sourcePath;
    private static String accessToken;
    private static String refreshToken;

    @BeforeEach
    void sourcePathInit() {
        String userId = UUID.randomUUID().toString();
        accessToken = accessToken == null ? accessTokenService.generateAccessToken(userId) : accessToken;
        refreshToken = refreshToken == null ? refreshTokenService.generateRefreshToken(userId) : refreshToken;
        sourcePath = Paths.get(System.getProperty("user.home"),"git","crichton","tests","c++-samples").toString();
    }

    private static boolean checkCoyoteCli() throws IOException {
        String symbolicLink = "/usr/bin/coyoteCli";
        Path path = FileSystems.getDefault().getPath(symbolicLink);
        return !Files.isSymbolicLink(path);
    }

    private static byte[] readProjectSettingFile(String jsonFilePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(jsonFilePath);
        InputStream inputStream = resource.getInputStream();
        return inputStream.readAllBytes();
    }

    private Path createZipFile(String zipFileName, byte[] content) throws IOException {
        Path zipFilePath = Files.createTempFile(zipFileName, "");
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFilePath, StandardOpenOption.WRITE))) {
            ZipEntry entry = new ZipEntry("projectSetting_default.json");
            zipOutputStream.putNextEntry(entry);
            zipOutputStream.write(content);
            zipOutputStream.closeEntry();
        }
        return zipFilePath;
    }


    @Test
    @Order(1)
    void getPlugin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/crichton/test/plugin")
                                              .header("Authorization", accessToken)
                                              .header("RefreshToken", refreshToken))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @Order(2)
    void doCoyoteCliAndProjectSetting() throws Exception {
        if (checkCoyoteCli()){
            return;
        }
        String jsonResourcePath = "projectSetting_default.json";
        Path zipFilePath = createZipFile("setting", readProjectSettingFile(jsonResourcePath));
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "setting.zip",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                Files.newInputStream(zipFilePath)
        );
        TestDTO.PluginRequest request = new TestDTO.PluginRequest();
        HashMap<String,String> settings = new HashMap<>();
        settings.put("report","crichton_unitTest.csv");
        settings.put("projectSetting", "projectSetting_default.json");
        request.setSourcePath(sourcePath);
        request.setPlugin("coyote");
        request.setPluginSettings(settings);
        MockMultipartFile data = new MockMultipartFile("data", "data", "application/json", mapper.writeValueAsBytes(request));
        String result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/crichton/test/plugin/run")
                                                              .file(data)
                                                              .file(file)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(mapper.writeValueAsString(request))
                                                              .header("Authorization", accessToken)
                                                              .header("RefreshToken", refreshToken))
                               .andExpect(MockMvcResultMatchers.status().isOk())
                               .andDo(MockMvcResultHandlers.print())
                               .andReturn()
                               .getResponse()
                               .getContentAsString()
                               .replaceAll("^\"|\"$", "");
        TestDTO.TestResponse response = mapper.readValue(result ,TestDTO.TestResponse.class);
        assertTrue(response.getTestResult());
    }




    @Test
    @Order(3)
    void doPluginTestBlankException() throws Exception {
        TestDTO.PluginRequest request = new TestDTO.PluginRequest();
        request.setSourcePath("");
        MockMultipartFile data = new MockMultipartFile("data", "data", "application/json", mapper.writeValueAsBytes(request));

        String result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/crichton/test/plugin/run")
                                              .file(data)
                                              .header("Authorization", accessToken)
                                              .header("RefreshToken", refreshToken))
               .andExpect(MockMvcResultMatchers.status().is5xxServerError())
               .andDo(print())
               .andReturn()
               .getResponse()
               .getContentAsString()
               .replaceAll("^\"|\"$", "");

      GlobalExceptionResponse response = mapper.readValue(result ,GlobalExceptionResponse.class);
      assertEquals("F001", response.getCode());
    }

    @Test
    @Order(4)
    void doPluginTestNotFileExistException() throws Exception {
        TestDTO.PluginRequest request = new TestDTO.PluginRequest();
        request.setSourcePath("%2ka1oll");
        MockMultipartFile data = new MockMultipartFile("data", "data", "application/json", mapper.writeValueAsBytes(request));

        String result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/crichton/test/plugin/run")
                                                              .file(data)
                                                              .header("Authorization", accessToken)
                                                              .header("RefreshToken", refreshToken))
                               .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                               .andDo(print())
                               .andReturn()
                               .getResponse()
                               .getContentAsString()
                               .replaceAll("^\"|\"$", "");

        GlobalExceptionResponse response = mapper.readValue(result ,GlobalExceptionResponse.class);
        assertEquals("F001", response.getCode());
    }

    @Test
    @Order(5)
    void doPluginTestNotPluginExistException() throws Exception {
        TestDTO.PluginRequest request = new TestDTO.PluginRequest();
        request.setSourcePath(sourcePath);
        request.setPlugin("NotPluginExceptionTest");

        MockMultipartFile data = new MockMultipartFile("data", "data", "application/json", mapper.writeValueAsBytes(request));

        String result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/crichton/test/plugin/run")
                                                              .file(data)
                                                              .header("Authorization", accessToken)
                                                              .header("RefreshToken", refreshToken))
                               .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                               .andDo(print())
                               .andReturn()
                               .getResponse()
                               .getContentAsString()
                               .replaceAll("^\"|\"$", "");

        GlobalExceptionResponse response = mapper.readValue(result ,GlobalExceptionResponse.class);
        assertEquals("F004", response.getCode());
    }


    @Test
    @Order(6)
    void getCrichtonLog() throws Exception{
        LogDTO.LogRequest request = new LogDTO.LogRequest();
        request.setMaxline(-1);
        request.setStartpos(0);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/crichton/test/log")
                                              .header("Authorization", accessToken)
                                              .header("RefreshToken", refreshToken)
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(mapper.writeValueAsString(request)))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andDo(MockMvcResultHandlers.print());
    }



}