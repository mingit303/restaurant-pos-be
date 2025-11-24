package com.example.restaurant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Cho phép truy cập thư mục uploads/images/
        String uploadPath = System.getProperty("user.dir") + "/uploads/images/";
        System.out.println("📂 Serving static files from: " + uploadPath);

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/" + uploadPath.replace("\\", "/"));
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }
}
