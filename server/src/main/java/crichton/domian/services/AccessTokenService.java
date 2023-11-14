package crichton.domian.services;

import crichton.domian.dtos.PayloadDTO;

public interface AccessTokenService {

    String generateAccessToken(String userId);

    boolean validateAccessToken(PayloadDTO payloadDTO);

    boolean isAccessTokenExpired(PayloadDTO payloadDTO);

    String refreshAccessToken(PayloadDTO payloadDTO);

}
