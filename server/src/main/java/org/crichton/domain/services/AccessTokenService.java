package crichton.domain.services;

import crichton.domain.dtos.PayloadDTO;

public interface AccessTokenService {

    String generateAccessToken(String userId);

    boolean validateAccessToken(String token, PayloadDTO payloadDTO);

    boolean isAccessTokenExpired(PayloadDTO payloadDTO);

    String refreshAccessToken(String token, PayloadDTO payloadDTO);

    String generatePayload(String userId, long expirationTime);

    String generateSignature(String payload);
}
