package com.popflix.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme cookieAuth = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("access_token");

        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("cookieAuth", cookieAuth)
                        .addSecuritySchemes("bearerAuth", bearerAuth))
                .info(new Info()
                        .title("PopFlix API")
                        .version("1.0.0")
                        .description("PopFlix API Documentation<br><br>" +
                                "인증 방식:<br>" +
                                "1. Cookie Authentication (Production)<br>" +
                                "2. Bearer Token Authentication (Development/Test)<br><br>" +
                                "개발 테스트 시에는 Bearer Token 방식을 사용하시면 됩니다."))
                .addSecurityItem(new SecurityRequirement().addList("cookieAuth"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}