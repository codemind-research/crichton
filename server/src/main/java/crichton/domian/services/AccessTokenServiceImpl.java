package crichton.domian.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import crichton.domian.dtos.PayloadDTO;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("AccessTokenService")
@RequiredArgsConstructor
public class AccessTokenServiceImpl implements AccessTokenService{

    //TODO : SECRET_KEY DB 추가되면 DB 에저장
    private final static byte[] SECRET_KEY = generateSecretKey();

    private final ObjectMapper objectMapper;

    private Long expirationTimeMillis = 60 * 60 *1000L; //1시간 제한

    private static byte[] generateSecretKey() {
        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);
        return keyBytes;
    }

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

    private String generateSignature(String payload) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY, "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] signatureBytes = sha256Hmac.doFinal(payload.getBytes("UTF-8"));

            return Base64.encodeBase64URLSafeString(signatureBytes);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }


    @Override
    public String generateAccessToken(String userId) {
        long expirationTime = System.currentTimeMillis() + expirationTimeMillis;
        String token = UUID.randomUUID().toString();
        String payload = generatePayload(userId,expirationTime);
        String signature = generateSignature(payload);
        return token + "." +payload + "." + signature;
    }

    // 무결성 검사 추가
    @Override
    public boolean validateAccessToken(String token, PayloadDTO payloadDTO) {
        String[] parts = token.split("\\.");
        if (parts.length != 3 || payloadDTO == null) {
            return false; // 토큰 형식이 잘못되었음
        }

        String payload = parts[1];
        String signature = parts[2];
        // 서명 검증
        String expectedSignature = generateSignature(payload);
        return expectedSignature.equals(signature) && !isAccessTokenExpired(payloadDTO);
    }

    @Override
    public boolean isAccessTokenExpired(PayloadDTO payloadDTO) {
        return System.currentTimeMillis() > payloadDTO.getExp();
    }

    @Override
    public String refreshAccessToken(String token, PayloadDTO payloadDTO) {
        if (!validateAccessToken(token, payloadDTO)) {
            return generateAccessToken(payloadDTO.getSub());
        } else {
            return null;
        }
    }

}
