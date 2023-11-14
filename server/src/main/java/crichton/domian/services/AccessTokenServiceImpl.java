package crichton.domian.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import crichton.domian.dtos.PayloadDTO;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("AccessTokenService")
@RequiredArgsConstructor
public class AccessTokenServiceImpl implements AccessTokenService{

    private final ObjectMapper objectMapper;

    private Long expirationTimeMillis = 60 * 60 *1000L; //1시간 제한

    private String generatePayload(String userId, long expirationTime) {
        try {
            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("sub", userId);
            payloadMap.put("exp", expirationTime);
            return Base64.encodeBase64URLSafeString(objectMapper.writeValueAsString(payloadMap).getBytes());
        }catch (IOException e){
            throw new RuntimeException("Error converting map to JSON", e);
        }
    }


    @Override
    public String generateAccessToken(String userId) {
        long expirationTime = System.currentTimeMillis() + expirationTimeMillis;
        String token = UUID.randomUUID().toString();
        return token + "." +generatePayload(userId, expirationTime);
    }

    //TODO: 무결성 검사 추가
    @Override
    public boolean validateAccessToken(PayloadDTO payloadDTO) {
        if (payloadDTO != null) {
            return !isAccessTokenExpired(payloadDTO);
        }
        return false;
    }

    @Override
    public boolean isAccessTokenExpired(PayloadDTO payloadDTO) {
        return System.currentTimeMillis() > payloadDTO.getExp();
    }

    @Override
    public String refreshAccessToken(PayloadDTO payloadDTO) {
        if (!validateAccessToken(payloadDTO)) {
            return generateAccessToken(payloadDTO.getSub());
        } else {
            return null;
        }
    }

}
