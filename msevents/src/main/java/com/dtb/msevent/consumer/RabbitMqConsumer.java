package com.dtb.msevent.consumer;

import com.dtb.msevent.dto.request.MessageEvent;
import com.dtb.msevent.util.logging.Logging;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RabbitMqConsumer {

    private final Logging logging = new Logging();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "${app.response-config.queue}")
    public void receiveMessage(Message message) {
        String body = new String(message.getBody());
        try {
            MessageEvent event = objectMapper.readValue(body, MessageEvent.class);

            logging
                    .setLogLevel("info")
                    .setTransactionID(UUID.randomUUID().toString())
                    .setProcess("RabbitMqConsumer")
                    .setRequestString(body)
                    .setResponseMsg("Received event for email: " + event.getEmail())
                    .write();

        } catch (Exception e) {
            logging
                    .setLogLevel("error")
                    .setTransactionID(UUID.randomUUID().toString())
                    .setProcess("RabbitMqConsumer")
                    .setRequestString(body)
                    .setResponseMsg("Failed to deserialize or process message: " + e.getMessage())
                    .write();
        }
    }
}
