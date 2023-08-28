package com.lazeez;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableWebMvc
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*") // Allow any origin
                        .allowedMethods("*") // Allow any HTTP method (GET, POST, etc.)
                        .allowedHeaders("Content-Type", "Authorization"); // Allow specific headers
            }
        };
}}