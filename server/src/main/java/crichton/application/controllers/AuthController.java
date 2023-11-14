package crichton.application.controllers;

import crichton.domian.dtos.AuthDTO;
import crichton.domian.services.AccessTokenService;
import crichton.domian.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@CrossOrigin
@RestController("AuthController")
@RequestMapping("/api/v1/crichton/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/token")
    public ResponseEntity<AuthDTO.TokenResponse> getToken(){
        String userId = UUID.randomUUID().toString();
        return ResponseEntity.ok()
                              .body(AuthDTO.TokenResponse
                              .builder()
                              .accessToken(accessTokenService.generateAccessToken(userId))
                              .refreshToken(refreshTokenService.generateRefreshToken(userId))
                              .build());
    }

}
