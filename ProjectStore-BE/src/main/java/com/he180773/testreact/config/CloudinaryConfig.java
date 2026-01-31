package com.he180773.testreact.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "drmxwwp7w",
                "api_key", "975677897713668",
                "api_secret", "zio77NODusqOEXT25E4_dssTvWQ"
        ));
    }
}
