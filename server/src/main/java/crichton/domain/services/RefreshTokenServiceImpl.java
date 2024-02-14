package crichton.domain.services;

import crichton.domain.dtos.RefreshTokenDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("RefreshTokenService")
public class RefreshTokenServiceImpl implements RefreshTokenService {

    //TODO: In the future, change Map data to a database.
    private Map<String, RefreshTokenDTO> tokenStoreMap = new HashMap<>();
    private Long expirationTimeMillis = 15 * 24 * 60 * 60 * 1000L; //15 days limit

    //TODO: In the future, check whether there is a duplicate userId when adding to the database.
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
            tokenStoreMap.remove(customTokenDTO.getToken());
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void addRefreshToken(String userId, String token, Long expiredDate) {
        tokenStoreMap.put(userId, RefreshTokenDTO.builder()
                                                 .token(token)
                                                 .expireDate(expiredDate)
                                                 .build());
    }

    @Override
    public void deleteRefreshToken(String userId) {
        tokenStoreMap.remove(userId);
    }
}
