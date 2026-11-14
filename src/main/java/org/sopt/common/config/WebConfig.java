package org.sopt.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String[] allowOrigins; // 요청을 허용할 프론트엔드 Origin

    WebConfig(@Value("${app.cors.allowed-origins}") String[] allowOrigins) {
        this.allowOrigins = allowOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // "/api/" URL 하위 경로에 CORS 정책 적용
                .allowedOrigins(allowOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH","DELETE") // 허용할 HTTP 메서드
                .allowedHeaders("Content-Type", "Authorization")  // 요청에 사용할 수 있는 HTTP 헤더
                .allowCredentials(false); // 교차 출처 요청의 쿠키 등 브라우저 자격 증명 허용 여부
    }

}
