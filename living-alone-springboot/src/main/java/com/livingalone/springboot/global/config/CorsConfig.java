package com.livingalone.springboot.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedOrigins("http://localhost:3000")
                .allowedOrigins("https://smucapstonedesign.netlify.app")
                .allowedOrigins("http://localhost:8081");
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:3000"); // 도메인
        corsConfiguration.addAllowedOrigin("http://localhost:8081");
        corsConfiguration.addAllowedOrigin("https://smucapstonedesign.netlify.app"); // 추가된 도메인
        corsConfiguration.addAllowedMethod("*"); // 메서드
        corsConfiguration.addAllowedHeader("*"); // 헤더
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }
}