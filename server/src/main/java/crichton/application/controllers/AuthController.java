package crichton.application.controllers;

import crichton.domain.dtos.AuthDTO;
import crichton.domain.services.AccessTokenService;
import crichton.domain.services.RefreshTokenService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Auth Controller", description = "APIs related to authentication. " +
        "This controller provides functionalities for testing and managing authentication processes.")
@CrossOrigin
@RestController("AuthController")
@RequestMapping("/api/v1/crichton/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/token")
    @Operation(summary = "Generate Token Information", description = "API for generating AccessToken for client usage and RefreshToken for server storage. " +
            "This endpoint is responsible for creating tokens that enable secure communication between the client and server.")
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
    @Operation(summary = "Delete Token Information", description = "Deletes RefreshToken information stored in server memory.")
    public void deleteRefreshToken(@RequestBody AuthDTO.TokenRequest request){
        refreshTokenService.deleteRefreshToken(request.getUserId());
    }

}
