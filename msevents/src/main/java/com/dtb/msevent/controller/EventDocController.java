package com.dtb.msevent.controller;

import com.dtb.msevent.dto.request.MessageEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Event Listener Documentation", description = "Documents events consumed from RabbitMQ")
public class EventDocController {

    @Operation(summary = "MessageEvent Listener", description = "Describes the expected message structure for 'my-queue'")
    @ApiResponse(responseCode = "200", description = "Accepted MessageEvent")
    @PostMapping("/docs/event-listener")
    public void messageListener(@RequestBody MessageEvent event) {
        // No-op: used only for documentation
    }
}
