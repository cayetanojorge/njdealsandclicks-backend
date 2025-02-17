package com.njdealsandclicks.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NJ - Deals and Clicks API")
                        .version("1.0.0")
                        .description("Documentazione degli endpoint REST del progetto")
                        .contact(new Contact()
                                .name("Supporto Tecnico")
                                .email("support@example.com")
                                .url("https://example.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentazione Completa")
                        .url("https://example.com/docs"));
    }
}

