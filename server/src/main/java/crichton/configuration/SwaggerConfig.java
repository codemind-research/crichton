package crichton.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


/**
 * Swagger API 문서를 생성하는 클래스
 * 추후 https 프로토콜을 사용할 경우 사용
 * 기본 URL 주소 : https://localhost:[port]/swagger-ui/index.html#/
 * 포트 번호나 IP 경우에 따라 알맞게 변경
 */
@Configuration("Swagger")
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
//                .securityContexts(List.of(securityContext()))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/v1/crichton/**"))
                .build()
                .apiInfo(apiInfo());
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Crichton interface Document")
                .description("Rest API")
                .version("1.0")
                .build();
    }

//    private SecurityContext securityContext() {
//        return SecurityContext.builder().build();
//    }
}
