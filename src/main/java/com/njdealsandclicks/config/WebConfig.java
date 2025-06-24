package com.njdealsandclicks.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.micrometer.common.lang.NonNull;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    // // from doc
	// @Override
	// public void addCorsMappings(@NonNull CorsRegistry registry) {

	// 	registry.addMapping("/api/**")
	// 		.allowedOrigins("https://domain2.com")
    //         .allowedOrigins("http://localhost:5173")
	// 		.allowedMethods("PUT", "DELETE")
	// 		.allowedHeaders("header1", "header2", "header3")
	// 		.exposedHeaders("header1", "header2")
	// 		.allowCredentials(true).maxAge(3600);

	// 	// Add more mappings...
	// }

    @Override
    public void addCorsMappings(@SuppressWarnings("null") @NonNull CorsRegistry registry) {

        // Frontend (accesso limitato)
        // registry.addMapping("/api/public/**")
        registry.addMapping("/api/public/**")
                .allowedOrigins("https://www.njdealsandclicks.com/", "http://localhost:5173")
                // .allowedMethods("GET", "POST")
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true);

        // Microservizi interni (accesso più ampio)
        // registry.addMapping("/api/internal/**")
        registry.addMapping("/api/**")
                .allowedOrigins("http://internal-service-nj.local", "http://localhost:8081")
                // .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true);
    }
}