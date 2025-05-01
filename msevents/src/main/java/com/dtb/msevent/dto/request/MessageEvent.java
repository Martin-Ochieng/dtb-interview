package com.dtb.msevent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Event sent by RabbitMQ")
public class MessageEvent {

    @JsonProperty
    @Schema(description = "The content of the message")
    private String message;

    @JsonProperty
    @Schema(description = "Email address")
    private String email;

}
