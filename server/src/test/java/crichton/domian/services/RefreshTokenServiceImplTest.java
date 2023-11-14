package crichton.domian.services;

import crichton.domian.dtos.RefreshTokenDTO;
import crichton.security.TokenInterceptor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class RefreshTokenServiceImplTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private TokenInterceptor tokenInterceptor;


    private static String userId;

    @BeforeAll
    public static void init () {
        userId = UUID.randomUUID().toString();
    }

    @Test
    void generateRefreshToken() {
        String refreshToken = refreshTokenService.generateRefreshToken(userId);
        assertTrue(refreshTokenService.validateRefreshToken(userId,refreshToken));
    }

    @Test
    void invalidateUserId() {
        assertFalse(refreshTokenService.validateRefreshToken("invalidUserId",""));
    }

    @Test
    void invalidateToken() {
        refreshTokenService.generateRefreshToken(userId);
        assertFalse(refreshTokenService.validateRefreshToken(userId,"invalidToken"));
    }

    @Test
    void invalidRefreshTokenExpired() {
        String token = UUID.randomUUID().toString();
        long expirationTimeMillis = System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000L;
        refreshTokenService.addRefreshToken(userId, token, expirationTimeMillis);
        RefreshTokenDTO dto =RefreshTokenDTO.builder().token(token).expireDate(expirationTimeMillis).build();
        assertTrue(refreshTokenService.isRefreshTokenExpired(dto));
    }

    @Test
    void isRefreshTokenExpired() {
        String token = UUID.randomUUID().toString();
        long expirationTimeMillis = System.currentTimeMillis() + 11 * 24 * 60 * 60 * 1000L;
        refreshTokenService.addRefreshToken(userId, token, expirationTimeMillis);
        RefreshTokenDTO dto =RefreshTokenDTO.builder().token(token).expireDate(expirationTimeMillis).build();
        assertFalse(refreshTokenService.isRefreshTokenExpired(dto));
    }

}