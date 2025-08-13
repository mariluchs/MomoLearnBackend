package com.example.momolearn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")                // gilt für alle Endpunkte (inkl. /api/**)
        .allowedOrigins("http://localhost:5173")
        .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
        .allowedHeaders("*")
        .exposedHeaders("Content-Disposition") // optional
        .allowCredentials(true)                // nur falls du Cookies/Auth brauchst
        .maxAge(3600);                         // Cache für Preflight
  }
}
