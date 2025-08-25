package com.example.momolearn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.momolearn.security.AuthInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final AuthInterceptor auth;

  public WebConfig(AuthInterceptor auth) {
    this.auth = auth;
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("http://localhost:5173")
        .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
        .allowedHeaders("*")
        .exposedHeaders("Content-Disposition")
        .allowCredentials(true)
        .maxAge(3600);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(auth)
        .addPathPatterns("/users/**")        // schützt alle /users/...-Routen
        .excludePathPatterns(
            "/auth/**",                      // Auth frei
            "/hello",                        // Debug frei
            "/debug/**"                      // Debug frei (dein DebugController hängt unter /api/debug)
        );
  }
}
