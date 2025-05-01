package com.dtb.mspayment.producer;


import com.dtb.mspayment.config.Configs;
import com.dtb.mspayment.dto.request.MessageEvent;
import com.dtb.mspayment.util.logging.Logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RabbitMqProducer {
    private final Configs configs;
    private final RabbitTemplate rabbitTemplate;
    private final Logging logging = new Logging();

    public void sendMessage(String message, String email) {



        try
        {

            Message messageString = getMessage(message, email);

            rabbitTemplate.send(configs.getResponseConfig().getExchange(),
                    configs.getResponseConfig().getRoutingKey(), messageString);


            logging
                    .setLogLevel("info")
                    .setTransactionID(UUID.randomUUID().toString())
                    .setProcess("RabbitMqProducer")
                    .setRequestString(message)
                    .setResponseMsg("Message sent to RabbitMQ")
                    .write();
        } catch (Exception e) {



            logging
                    .setLogLevel("error")
                    .setTransactionID(UUID.randomUUID().toString())
                    .setProcess("RabbitMqProducer")
                    .setRequestString(message)
                    .setResponseMsg(e.getMessage())
                    .write();
        }






    }

    private static Message getMessage(String message, String email) throws JsonProcessingException {
        MessageEvent event =  new MessageEvent(
                message,
                email
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(event);


        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");

        return new Message(jsonString.getBytes(), messageProperties);
    }


}
