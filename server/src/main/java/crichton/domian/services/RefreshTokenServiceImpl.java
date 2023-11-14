package crichton.domian.services;

import crichton.domian.dtos.RefreshTokenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("RefreshTokenService")
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    //TODO: 추후 Map 데이터를 DB로 변경
    private Map<String, RefreshTokenDTO> tokenStoreMap = new HashMap<>();
    private Long expirationTimeMillis = 15 * 24 * 60 * 60 * 1000L; //15일 제한

    //TODO: 추후 동일한 userId가 있는지 확인하기 db 추가할때 같이
    @Override
    public String generateRefreshToken(String userId) {
        long expirationTime = System.currentTimeMillis() + expirationTimeMillis;
        String token = UUID.randomUUID().toString();
        tokenStoreMap.put(userId, RefreshTokenDTO.builder()
                                                 .expireDate(expirationTime)
                                                 .token(token)
                                                 .build());
        return token;
    }

    @Override
    public boolean validateRefreshToken(String userId, String token) {
       RefreshTokenDTO customTokenDTO = tokenStoreMap.getOrDefault(userId,null);
        if (customTokenDTO != null) {
            return customTokenDTO.getToken().equals(token) && !isRefreshTokenExpired(customTokenDTO);
        }else {
            return false;
        }
    }

    @Override
    public boolean isRefreshTokenExpired(RefreshTokenDTO customTokenDTO) {
        if (System.currentTimeMillis() > customTokenDTO.getExpireDate()) {
            // 토큰이 만료되었으면 삭제
            tokenStoreMap.remove(customTokenDTO.getToken());
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void addRefreshToken(String userId, String token, Long expiredDate) {
        long expirationTime = System.currentTimeMillis() + expiredDate;
        tokenStoreMap.put(userId, RefreshTokenDTO.builder()
                                                 .token(token)
                                                 .expireDate(expirationTime)
                                                 .build());
    }
}
