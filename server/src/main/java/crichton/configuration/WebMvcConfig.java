package crichton.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration("WebMvc")
public class WebMvcConfig implements WebMvcConfigurer {

    /** 아직 제대로 안정함 추후 수정 예정**/
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")  // 모든 출처 허용, 필요에 따라 원하는 출처로 변경
                .allowedMethods("*")  // 모든 HTTP 메소드 허용, 필요에 따라 원하는 메소드로 변경
                .allowedHeaders("*")  // 모든 헤더 허용, 필요에 따라 원하는 헤더로 변경
                .exposedHeaders("Content-Type", "Accept", "Authorization")  // 클라이언트에 노출할 응답 헤더
                .allowCredentials(false)  // 자격 증명 정보 허용
                .maxAge(3600);  // 사전 요청 결과를 캐시하는 시간
    }
}
