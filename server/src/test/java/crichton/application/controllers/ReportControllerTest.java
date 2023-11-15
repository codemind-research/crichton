package crichton.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import crichton.application.exceptions.handler.GlobalExceptionResponse;
import crichton.domian.dtos.ReportDTO;
import crichton.domian.services.AccessTokenService;
import crichton.domian.services.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

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


    @Test
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
    void transformCsvData() throws Exception{
        ReportDTO.DataRequest request = new ReportDTO.DataRequest(sourcePath);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/crichton/report/data")
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(mapper.writeValueAsString(request))
                                                              .header("Authorization", accessToken)
                                                              .header("RefreshToken", refreshToken))
                               .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                               .andDo(print());

    }
}