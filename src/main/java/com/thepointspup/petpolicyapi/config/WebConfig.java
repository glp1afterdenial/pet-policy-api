package com.thepointspup.petpolicyapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins(
                        "https://thepointspup.com",
                        "https://www.thepointspup.com",
                        "https://daniellefuglestad.dev",
                        "https://www.daniellefuglestad.dev",
                        "https://pet-policy-api.onrender.com",
                        "http://localhost:1313",
                        "http://localhost:3000",
                        "http://localhost:8080"
                    )
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE");
            }
        };
    }
}
