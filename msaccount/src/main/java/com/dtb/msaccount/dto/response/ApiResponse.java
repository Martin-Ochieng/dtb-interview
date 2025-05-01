package com.dtb.msaccount.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ApiResponse {
    @JsonProperty
    private String responseCode;
    @JsonProperty
    private String responseMessage;
    @JsonProperty
    private String customerMessage;
    @JsonProperty
    private String refId;
    @JsonProperty
    private Object data;
}
