package com.dtb.msprofile.util.exceptionhandler;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class CustomException extends RuntimeException  {
    private final HttpStatus status;
    private final String requestRefId;
    private final String process;
    private final transient Object object;
    public CustomException(String message, HttpStatus status, String requestRefId, Object object, String process) {
        super(message);
        this.status = status;
        this.requestRefId = requestRefId;
        this.object = object;
        this.process = process;
    }

}

