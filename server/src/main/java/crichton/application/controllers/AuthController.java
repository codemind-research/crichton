package crichton.application.controllers;

import crichton.domain.dtos.AuthDTO;
import crichton.domain.services.AccessTokenService;
import crichton.domain.services.RefreshTokenService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController("AuthController")
@RequestMapping("/api/v1/crichton/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/token")
    @ApiOperation(value = "토큰 정보 생성", notes = "클라이언트에서 사용할 AccessToken 과 서버에 저장될 RefreshToken 을 생성하는 Api")
    public ResponseEntity<AuthDTO.TokenResponse> getToken(){
        String userId = UUID.randomUUID().toString();
        return ResponseEntity.ok()
                              .body(AuthDTO.TokenResponse
                              .builder()
                              .accessToken(accessTokenService.generateAccessToken(userId))
                              .refreshToken(refreshTokenService.generateRefreshToken(userId))
                              .build());
    }

    @DeleteMapping("/token")
    @ApiOperation(value = "토큰 정보 삭제", notes = "서버 메모리에 들고 있는 RefreshToken 정보 삭제")
    public void deleteRefreshToken(@RequestBody AuthDTO.TokenRequest request){
        refreshTokenService.deleteRefreshToken(request.getUserId());
    }

}
