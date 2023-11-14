package crichton.domian.services;

import crichton.domian.dtos.PayloadDTO;
import crichton.security.TokenInterceptor;
import crichton.util.ObjectMapperUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccessTokenServiceImplTest {

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private TokenInterceptor tokenInterceptor;

    private PayloadDTO generatePayloadDTO(String accessToken) {
        String[] parts = accessToken.split("\\.");
        String payload = parts[1];
        String decodePayload = tokenInterceptor.decodeBase64(payload);
        return ObjectMapperUtils.convertJsonStringToObject(decodePayload, PayloadDTO.class);
    }

    @Test
    void generateAccessToken() {
        String accessToken = accessTokenService.generateAccessToken(UUID.randomUUID().toString());
        PayloadDTO payloadDTO = generatePayloadDTO(accessToken);
        boolean result = accessTokenService.validateAccessToken(accessToken, payloadDTO);
        assertTrue(result);
    }

    //userId 가 UUID 형태로 안들어와도 토큰은 생성은 가능
    @Test
    void generateAccessTokenFromNonUUID() {
        String accessToken = accessTokenService.generateAccessToken("testetstetstetstestste");
        PayloadDTO payloadDTO = generatePayloadDTO(accessToken);
        boolean result = accessTokenService.validateAccessToken(accessToken, payloadDTO);
        assertTrue(result);
    }

    @Test
    void inValidateAccessToken() {
        PayloadDTO dto = PayloadDTO.builder().exp(16868291L).sub(UUID.randomUUID().toString()).build();
        boolean result = accessTokenService.validateAccessToken("test",dto);
        assertFalse(result);
    }

    @Test
    void inValidatePayLoadOfNull() {
        boolean payloadOfNull = accessTokenService
                .validateAccessToken(accessTokenService.generateAccessToken(UUID.randomUUID().toString()), null);
        assertFalse(payloadOfNull);
    }

    @Test
    void inValidatePayLoad() {
        String accessToken = accessTokenService.generateAccessToken(UUID.randomUUID().toString());
        String[] parts = accessToken.split("\\.");
        String token = parts[0];
        String signature = parts[2];
        PayloadDTO payloadDTO = generatePayloadDTO(accessToken);
        String forgedToken = token + "." + "checksumTest" + "." + signature;
        boolean forgedPayloadResult = accessTokenService
                .validateAccessToken(forgedToken, payloadDTO);
        assertFalse(forgedPayloadResult);
    }

    @Test
    void inValidateSignature() {

        String accessToken = accessTokenService.generateAccessToken(UUID.randomUUID().toString());
        String[] parts = accessToken.split("\\.");
        String token = parts[0];
        String payload = parts[1];
        PayloadDTO payloadDTO = generatePayloadDTO(accessToken);
        String forgedToken = token + "." + payload + "." + "checksumTest";
        boolean result = accessTokenService
                .validateAccessToken(forgedToken, payloadDTO);
        assertFalse(result);
    }


    @Test
    void isAccessTokenExpired() {

        long expirationTime = System.currentTimeMillis() - 62 * 60 *1000L;
        String token = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        String payload = accessTokenService.generatePayload(userId,expirationTime);
        String signature = accessTokenService.generateSignature(payload);
        String signedToken = token + "." +payload + "." + signature;
        PayloadDTO payloadDTO = generatePayloadDTO(signedToken);
        boolean result = accessTokenService
                .isAccessTokenExpired(payloadDTO);
        assertFalse(result);
    }



    @Test
    void refreshAccessToken() {
        long expirationTime = System.currentTimeMillis() - 62 * 60 *1000L;
        String token = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        String payload = accessTokenService.generatePayload(userId,expirationTime);
        String signature = accessTokenService.generateSignature(payload);
        String signedToken = token + "." +payload + "." + signature;
        PayloadDTO payloadDTO = generatePayloadDTO(signedToken);
        String refreshToken = accessTokenService.refreshAccessToken(signedToken,payloadDTO);
        PayloadDTO refreshPayload = generatePayloadDTO(refreshToken);
        assertTrue(accessTokenService.validateAccessToken(refreshToken,refreshPayload));
    }

    @Test
    void invalidDateRefreshAccessToken() {
        String accessToken = accessTokenService.generateAccessToken(UUID.randomUUID().toString());
        String[] parts = accessToken.split("\\.");
        String token = parts[0];
        String payload = parts[1];
        PayloadDTO payloadDTO = generatePayloadDTO(accessToken);
        String forgedToken = token + "." + payload + "." + "checksumTest";
        assertNull(accessTokenService.refreshAccessToken(forgedToken,payloadDTO));
    }
}