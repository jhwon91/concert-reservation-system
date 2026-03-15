package com.hhplus.concert.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    public static final String QUEUE_TOKEN_SCHEME = "QueueToken";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Concert Reservation API")
                        .description("콘서트 예약 서비스 API")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes(QUEUE_TOKEN_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-Queue-Token")
                                        .description("대기열 활성 토큰 (ACTIVE 상태여야 함)")));
    }
}
