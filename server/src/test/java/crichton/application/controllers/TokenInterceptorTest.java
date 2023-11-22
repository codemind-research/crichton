package crichton.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import crichton.application.exceptions.handler.GlobalExceptionResponse;
import crichton.domain.services.AccessTokenService;
import crichton.domain.services.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenInterceptorTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AccessTokenService accessTokenService;

    private static String userId;

    private String generateInvalidAccessToken() {
        String accessToken = accessTokenService.generateAccessToken(userId);
        String[] parts = accessToken.split("\\.");
        String token = parts[0];
        String payload = parts[1];
        return token + "." + payload + "." + "checksumTest";
    }

    private String generateOneHoursAgoToken() {
        long expirationTime = System.currentTimeMillis() - 62 * 60 *1000L;
        String token = UUID.randomUUID().toString();
        String payload = accessTokenService.generatePayload(userId,expirationTime);
        String signature = accessTokenService.generateSignature(payload);
        return token + "." +payload + "." + signature;
    }

    private String generateFifthDaysAgoToken() {
        String token = UUID.randomUUID().toString();
        long expirationTimeMillis = System.currentTimeMillis() - 16 * 24 * 60 * 60 * 1000L;
        refreshTokenService.addRefreshToken(userId, token, expirationTimeMillis);
        return token;
    }

    @BeforeEach
    void tokenInit() {
        userId = userId == null ?  UUID.randomUUID().toString() : userId;
    }


    @Test
    void successTokenInterceptor() throws Exception {
        String accessToken = accessTokenService.generateAccessToken(userId);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/crichton/test/progress")
                                              .header("Authorization", accessToken))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void invalidTokenInterceptor() throws Exception {
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/crichton/test/progress")
                                              .header("Authorization", "%hk21iga!"))
               .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                               .andDo(MockMvcResultHandlers.print())
                               .andReturn()
                               .getResponse()
                               .getContentAsString()
                               .replaceAll("^\"|\"$", "");
        GlobalExceptionResponse response = mapper.readValue(result ,GlobalExceptionResponse.class);
        assertEquals("T001", response.getCode());
    }

    @Test
    void invalidAccessTokenInterceptor() throws Exception {
        String invalidAccessToken = generateInvalidAccessToken();
        String refreshToken = refreshTokenService.generateRefreshToken(userId);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/crichton/test/progress")
                                              .header("Authorization", invalidAccessToken)
                                              .header("RefreshToken", refreshToken))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void isExpiredAccessTokenInterceptor() throws Exception {
        String oneHoursAgoToken = generateOneHoursAgoToken();
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/crichton/test/progress")
                                                              .header("Authorization", oneHoursAgoToken))
                               .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                               .andDo(MockMvcResultHandlers.print())
                               .andReturn()
                               .getResponse()
                               .getContentAsString()
                               .replaceAll("^\"|\"$", "");
        GlobalExceptionResponse response = mapper.readValue(result ,GlobalExceptionResponse.class);
        assertEquals("T001", response.getCode());
    }

    @Test
    void refreshAccessTokenInterceptor() throws Exception {
        String oneHoursAgoToken = generateOneHoursAgoToken();
        String refreshToken = refreshTokenService.generateRefreshToken(userId);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/crichton/test/progress")
                                                              .header("Authorization", oneHoursAgoToken)
                                                              .header("RefreshToken",refreshToken ))
                               .andExpect(MockMvcResultMatchers.status().isOk())
                               .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void isExpiredRefreshTokenInterceptor() throws Exception {
        String oneHoursAgoToken = generateOneHoursAgoToken();
        String fifthDaysAgoToken = generateFifthDaysAgoToken();
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/crichton/test/progress")
                                                              .header("Authorization", oneHoursAgoToken)
                                                              .header("RefreshToken", fifthDaysAgoToken))
                               .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                               .andDo(MockMvcResultHandlers.print())
                               .andReturn()
                               .getResponse()
                               .getContentAsString()
                               .replaceAll("^\"|\"$", "");
        GlobalExceptionResponse response = mapper.readValue(result ,GlobalExceptionResponse.class);
        assertEquals("T002", response.getCode());
    }

}
