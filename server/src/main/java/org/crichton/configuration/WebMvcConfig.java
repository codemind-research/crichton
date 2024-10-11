package org.crichton.configuration;

import org.crichton.domain.services.AccessTokenService;
import org.crichton.domain.services.RefreshTokenService;
import org.crichton.security.TokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
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
                .addPathPatterns("/api/**"); // Specify the URL pattern to apply.
    }
}
