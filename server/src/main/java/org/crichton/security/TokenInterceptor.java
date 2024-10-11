package crichton.security;

import crichton.application.exceptions.CustomException;
import crichton.application.exceptions.code.TokenErrorCode;
import crichton.domain.dtos.PayloadDTO;
import crichton.domain.services.AccessTokenService;
import crichton.domain.services.RefreshTokenService;
import crichton.util.ObjectMapperUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Base64;

@Component("TokenInterceptor")
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final static String HEADER_AUTH = "Authorization";
    private final static String REFRESH_TOKEN = "RefreshToken";
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;

    public String decodeBase64(String base64) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        return new String(decodedBytes);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws CustomException {
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/v1/crichton/auth/token")) {
            return true;
        }
        try {
            String accessToken = request.getHeader(HEADER_AUTH);
            String refreshToken = request.getHeader(REFRESH_TOKEN);
            String[] tokenParts = accessToken.split("\\.");
            String payload = decodeBase64(tokenParts[1]);
            PayloadDTO payloadDTO = ObjectMapperUtils.convertJsonStringToObject(payload, PayloadDTO.class);
            // 1. Verify the integrity and expiration date of the accessToken.
            if (accessTokenService.validateAccessToken(accessToken, payloadDTO) && !accessTokenService.isAccessTokenExpired(payloadDTO)){
                return true;
            }
            //2. Include the refreshToken value in the header when it is sent, and check if the refreshToken period has not expired.
            if (refreshToken != null && refreshTokenService.validateRefreshToken(payloadDTO.getSub(),refreshToken)) {
                String newAccessToken = accessTokenService.refreshAccessToken(accessToken, payloadDTO);
                //3. Include the newly issued accessToken in the header upon successful generation.
                response.setHeader("Authorization", newAccessToken);
                return true;
            }
            //4. Check if the refreshToken value is not included in the header or if the refreshToken period has expired.
            else {
                // 5. Verify whether the refreshToken value is not included in the header.
                if (refreshToken != null){
                    // This branch corresponds to when the refreshToken period has expired.
                    throw new CustomException(TokenErrorCode.INVALID_REFRESH_TOKEN);
                }
                else {
                    // This branch corresponds to when the accessToken period has expired.
                    throw new CustomException(TokenErrorCode.INVALID_ACCESS_TOKEN);
                }
            }
        }
        catch (CustomException customException) {
            throw customException;
        }
        catch (Exception e){
            throw new CustomException(TokenErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }
}
