package crichton.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import crichton.domain.dtos.LogDTO;
import crichton.domain.services.AccessTokenService;
import crichton.domain.services.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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

    private static String accessToken;
    private static String refreshToken;

    @BeforeEach
    void sourcePathInit() {
        String userId = UUID.randomUUID().toString();
        accessToken = accessToken == null ? accessTokenService.generateAccessToken(userId) : accessToken;
        refreshToken = refreshToken == null ? refreshTokenService.generateRefreshToken(userId) : refreshToken;
    }

    @Test
    void getData() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/crichton/report/data")
                                              .header("Authorization", accessToken)
                                              .header("RefreshToken", refreshToken))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andDo(MockMvcResultHandlers.print());
    }
}