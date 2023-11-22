package crichton.domain.services;

import crichton.domain.dtos.RefreshTokenDTO;

public interface RefreshTokenService {

    String generateRefreshToken(String userId);

    boolean validateRefreshToken(String userId, String token);

    boolean isRefreshTokenExpired(RefreshTokenDTO customTokenDTO);

    void addRefreshToken(String userId, String token, Long expiredDate);

    void deleteRefreshToken(String userId);

}
