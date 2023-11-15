package crichton.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import crichton.domian.services.AccessTokenService;
import crichton.domian.services.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

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
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/crichton/test/progress")
                                              .header("Authorization", "%hk21iga!"))
               .andExpect(MockMvcResultMatchers.status().isUnauthorized())
               .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void invalidAccessTokenInterceptor() throws Exception {
        String invalidAccessToken = generateInvalidAccessToken();
        String refreshToken = refreshTokenService.generateRefreshToken(userId);
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/crichton/test/progress")
                                              .header("Authorization", invalidAccessToken)
                                              .header("RefreshToken", refreshToken))
               .andExpect(MockMvcResultMatchers.status().isUnauthorized())
               .andDo(MockMvcResultHandlers.print())
               .andReturn()
               .getResponse()
               .getContentAsString()
               .replaceAll("^\"|\"$", "");
        AssertionErrors.assertEquals("AccessToken 예외 메세지가 일치하지 않습니다.","The access token provided is invalid" , result);
    }

    @Test
    void isExpiredAccessTokenInterceptor() throws Exception {
        String oneHoursAgoToken = generateOneHoursAgoToken();
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/crichton/test/progress")
                                                              .header("Authorization", oneHoursAgoToken))
                               .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                               .andDo(MockMvcResultHandlers.print())
                               .andReturn()
                               .getResponse()
                               .getContentAsString()
                               .replaceAll("^\"|\"$", "");
        AssertionErrors.assertEquals("AccessToken 예외 메세지가 일치하지 않습니다.","AccessToken has Expired" , result);
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
                               .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                               .andDo(MockMvcResultHandlers.print())
                               .andReturn()
                               .getResponse()
                               .getContentAsString()
                               .replaceAll("^\"|\"$", "");
        AssertionErrors.assertEquals("RefreshToken 예외 메세지가 일치하지 않습니다.","RefreshToken has Expired" , result);
    }

}
