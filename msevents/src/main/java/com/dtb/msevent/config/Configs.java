package com.dtb.msevent.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationPropertiesScan
@Component
@ConfigurationProperties("app")
public class Configs {

    private ResponseConfig responseConfig;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseConfig {
        private String successResponseCode;
        private String successResponseMessage;
        private String successCustomerMessage;

        private String failedResponseCode;
        private String failedResponseMessage;
        private String failedCustomerMessage;

        private String errorResponseCode;
        private String errorResponseMessage;
        private String errorCustomerMessage;

        private String customerExistsResponseMessage;
        private String customerExistsCustomerMessage;

        private String customerNotFoundResponseMessage;
        private String customerNotFoundCustomerMessage;

        private String invalidCredentialsResponseMessage;
        private String invalidCredentialsCustomerMessage;

        private String exchange;
        private String routingKey;
        private String queue;

    }





}
