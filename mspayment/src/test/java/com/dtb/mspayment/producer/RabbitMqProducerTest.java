package com.dtb.mspayment.producer;


import com.dtb.mspayment.config.Configs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class RabbitMqProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Configs configs;



    @InjectMocks
    private RabbitMqProducer producer;



    private Configs mockConfigs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockConfigs = new Configs(
                new Configs.ResponseConfig(
                        "0", // successResponseCode
                        "Request successful", // successResponseMessage
                        "Request Executed Successfully", // successCustomerMessage
                        "1", // failedResponseCode
                        "Request failed", // failedResponseMessage
                        "Request Execution Failed", // failedCustomerMessage
                        "2", // errorResponseCode
                        "An error occurred while processing your request", // errorResponseMessage
                        "An error occurred while processing your request", // errorCustomerMessage
                        "Customer already exists", // customerExistsResponseMessage
                        "Customer already exists with the given Email", // customerExistsCustomerMessage
                        "Customer Not Found", // invalidCredentialsResponseMessage
                        "Customer Not Found", // invalidCredentialsCustomerMessage
                        "Invalid Credentials", // customerNotFoundResponseMessage
                        "Invalid Credentials", // customerNotFoundCustomerMessage
                        "exchage",
                        "routingKey" // customerNotFoundCustomerMessage
                )
        );

        producer = new RabbitMqProducer(configs, rabbitTemplate);
    }

    @Test
    void testSendMessageSuccess(CapturedOutput output) {
        String messageContent = "test content";
        String email = "test@example.com";
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());

        // Act
        producer.sendMessage(messageContent, email);

        Assertions.assertTrue(output.getOut().contains("Message sent to RabbitMQ"));

    }

    @Test
    void testSendMessageFailure_shouldLogError(CapturedOutput output) {
        String messageContent = "error content";
        String email = "fail@example.com";
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());

        doThrow(new IllegalArgumentException("Simulated send failure"))
                .when(rabbitTemplate)
                .send(anyString(), anyString(), any(Message.class));

        producer.sendMessage(messageContent, email);

        // Act
        producer.sendMessage(messageContent, email);

        Assertions.assertTrue(output.getOut().contains("Simulated send failure"));
    }
}
