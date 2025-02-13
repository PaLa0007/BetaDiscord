package com.beta_discord_api.betadiscordapi;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow all endpoints
                .allowedOrigins("http://localhost:4200") // Frontend origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // Allow cookies and credentials
    }
}
