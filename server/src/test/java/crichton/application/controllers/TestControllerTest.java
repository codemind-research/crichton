package crichton.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import crichton.application.exceptions.handler.GlobalExceptionResponse;
import crichton.domain.dtos.ReportDTO;
import crichton.domain.dtos.TestDTO;
import crichton.domain.services.AccessTokenService;
import crichton.domain.services.RefreshTokenService;
import crichton.enumeration.TestResult;
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private static MockMultipartFile convert(String jsonFilePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(jsonFilePath);
        InputStream inputStream = resource.getInputStream();
        byte[] content = inputStream.readAllBytes();

        // MockMultipartFile 생성
        return new MockMultipartFile("file", resource.getFilename(), "application/json", content);
    }

    private static boolean checkCoyoteCli() throws IOException {
        String symbolicLink = "/usr/bin/coyoteCli";
        Path path = FileSystems.getDefault().getPath(symbolicLink);
        return !Files.isSymbolicLink(path);
    }



    @Test
    @Order(1)
    void doUnitTestBlankException() throws Exception {
        TestDTO.UnitTestRequest request = new TestDTO.UnitTestRequest();
        request.setSourcePath("");
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/crichton/test/unit/run")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(mapper.writeValueAsString(request))
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
    @Order(2)
    void doUnitTestNotFileExistException() throws Exception {
        TestDTO.UnitTestRequest request = new TestDTO.UnitTestRequest();
        request.setSourcePath("%2ka1oll");
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/crichton/test/unit/run")
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(mapper.writeValueAsString(request))
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
    @Order(3)
    void doUnitTest() throws Exception {
        if (checkCoyoteCli()){
            return;
        }
        TestDTO.UnitTestRequest request = new TestDTO.UnitTestRequest();
        request.setSourcePath(sourcePath);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/crichton/test/unit/run")
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
        assertEquals(TestResult.SUCCESS,response.getTestResult());
    }

    @Test
    @Order(4)
    void doUnitTestAndProjectSetting() throws Exception {
        if (checkCoyoteCli()){
            return;
        }
        TestDTO.UnitTestRequest request = new TestDTO.UnitTestRequest();
        request.setSourcePath(sourcePath);
        String jsonResourcePath = "projectSetting_default.json";
        MockMultipartFile multipartFile = convert(jsonResourcePath);

        String result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/crichton/test/unit/run")
                                                              .file(multipartFile)
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
        assertEquals(TestResult.SUCCESS,response.getTestResult());
    }
    
    @Test
    @Order(5)
    void getProgress() throws Exception {
        if (checkCoyoteCli()){
            return;
        }
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/crichton/test/progress")
                                              .header("Authorization", accessToken)
                                              .header("RefreshToken", refreshToken))
                               .andExpect(MockMvcResultMatchers.status().isOk())
                               .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(6)
    void notExistReport() throws Exception{
        ReportDTO.DataRequest request = new ReportDTO.DataRequest("");
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/crichton/report/data")
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(mapper.writeValueAsString(request))
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
    @Order(7)
    void transformCsvData() throws Exception{
        if (checkCoyoteCli()){
            return;
        }
        ReportDTO.DataRequest request = new ReportDTO.DataRequest(sourcePath);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/crichton/report/data")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(mapper.writeValueAsString(request))
                                              .header("Authorization", accessToken)
                                              .header("RefreshToken", refreshToken))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andDo(print());

    }


}