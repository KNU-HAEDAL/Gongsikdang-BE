package com.food.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gongsikdang API")
                        .version("1.0")
                        .description("🚀 **JWT 인증 사용법** 🚀  \n\n"
                                + "1️⃣ 먼저 `/user/login` API를 호출해 JWT 토큰을 받습니다.  \n"
                                + "2️⃣ 우측 상단의 `🔓 Authorize` 버튼을 클릭합니다.  \n"
                                + "3️⃣ `Bearer <토큰값>` 형식으로 입력 후 인증합니다.  \n"
                                + "4️⃣ 이후 인증이 필요한 API를 호출하면 자동으로 토큰이 포함됩니다.  \n\n"
                                + "✅ 회원가입(`/user/register`)과 로그인(`/user/login`)은 인증 없이 사용 가능합니다.")
                )
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                );
    }
}

