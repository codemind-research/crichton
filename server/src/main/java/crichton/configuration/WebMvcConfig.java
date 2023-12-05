package crichton.configuration;

import crichton.domain.services.AccessTokenService;
import crichton.domain.services.RefreshTokenService;
import crichton.security.TokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration("WebMvc")
@RequiredArgsConstructor
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenInterceptor(refreshTokenService,accessTokenService))
                .addPathPatterns("/api/**"); // 적용할 URL 패턴을 지정
    }
}
