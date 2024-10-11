package crichton.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.List;


/**
 Class responsible for generating Swagger API documentation.
 Intended for future use with the HTTPS protocol.
 Default URL: https://localhost:[port]/swagger-ui/index.html#/
 Adjust the port number or IP address accordingly.
 */
@OpenAPIDefinition(
        info = @Info(title = "Crichton API Documentation",
                description = "To understand the Swagger documentation and test the API, " +
                        "you need to first execute the GET METHOD /token API in the Auth Controller to obtain token information.<br>" +
                        " Then, click the Authorize button of the desired API, and enter the information of the obtained AccessToken.",
                version = "v1"))
@Configuration("Swagger")
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP).description("AccessToken").scheme("bearer").bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER).name("Authorization");
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Authorization");

        return new OpenAPI()
                .components(new Components())
                .security(Arrays.asList(securityRequirement));
    }

}
