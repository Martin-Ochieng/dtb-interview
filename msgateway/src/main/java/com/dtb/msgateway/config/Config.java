package com.dtb.msgateway.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
@Component
@ConfigurationPropertiesScan
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("app")
public class Config {
    private AuthConfig authConfig;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public  static class AuthConfig {
        private String username;
        private String password;
        private int jwtExpirationMs;
        private String jwtSecret;


    }

}
