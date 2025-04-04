package com.Compulynx.student_test_db.FileHandling;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImgConfig implements WebMvcConfigurer {

    String SAVE_DIR2 = System.getProperty("user.home") + "/Downloads/excel/";
    String DIR = System.getProperty("user.home") + "/var/log/applications/API/StudentPhotos/";
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + DIR + "/");
    }
}
