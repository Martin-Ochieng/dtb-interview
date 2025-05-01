package com.dtb.msevent.consumer;

import com.dtb.msevent.dto.request.MessageEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class RabbitMqConsumerTest {

    @InjectMocks
    private RabbitMqConsumer consumer;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        consumer= new RabbitMqConsumer();
    }

    @Test
    void shouldLogInfoWhenMessageIsValid(CapturedOutput output) throws Exception {
        // given
        MessageEvent event = new MessageEvent("Hello from Rabbit", "test@example.com");
        String json = objectMapper.writeValueAsString(event);
        Message amqpMessage = new Message(json.getBytes(), new MessageProperties());
        consumer.receiveMessage(amqpMessage);


        // when
        consumer.receiveMessage(amqpMessage);

        Assertions.assertTrue(output.getOut().contains("Hello from Rabbit"));


    }

    @Test
    void shouldLogErrorWhenMessageIsInvalid(CapturedOutput output) {
        // given
        String invalidJson = "this is not valid json";
        Message amqpMessage = new Message(invalidJson.getBytes(), new MessageProperties());

        // when
        consumer.receiveMessage(amqpMessage);

        Assertions.assertTrue(output.getOut().contains("Failed to deserialize or process message"));


    }
}
