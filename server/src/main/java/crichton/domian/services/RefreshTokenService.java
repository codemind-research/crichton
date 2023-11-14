package crichton.domian.services;

import crichton.domian.dtos.RefreshTokenDTO;

public interface RefreshTokenService {

    String generateRefreshToken(String userId);

    boolean validateRefreshToken(String userId, String token);

    boolean isRefreshTokenExpired(RefreshTokenDTO customTokenDTO);

    void addRefreshToken(String userId, String token);

}
