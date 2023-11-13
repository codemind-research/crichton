package crichton.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 여기에서 토큰 검증 및 처리 로직을 구현합니다.
        // 예를 들어, request.getHeader("Authorization")로 헤더에서 토큰을 가져오고 검증합니다.

        // 만약 토큰이 유효하면 true를 반환하고, 그렇지 않으면 false를 반환합니다.
        // 유효하지 않은 경우, response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 등을 사용하여 응답 상태를 설정할 수 있습니다.

        return true;
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
