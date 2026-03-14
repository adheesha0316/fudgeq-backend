package com.fudgeq.api.config;

import com.fudgeq.api.dto.NotificationResponseDto;
import com.fudgeq.api.entity.Notification;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        // Explicit mapping for Notification to NotificationResponseDto
        modelMapper.addMappings(new PropertyMap<Notification, NotificationResponseDto>() {
            @Override
            protected void configure() {
                // Map createdAt from BaseEntity to DTO
                map().setCreatedAt(source.getCreatedAt());

                // Map isRead boolean status correctly
                map().setRead(source.isRead());
            }
        });

        return modelMapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}
