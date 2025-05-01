package com.dtb.msaccount.util.logging;


import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Data
@Accessors(chain = true)
@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("java:S3776")
public class Logging {
    Object transactionID;
    String transaction;
    @Getter
    String logLevel;
    String targetSystem;
    Object responseCode = 200;
    Object response;
    String responseString;
    String responseMsg = "Success";
    Object transactionCost;
    String sourceSystem;
    String process;
    Object processDuration;
    Object request;
    String requestString;


    private final Logger logger;

    private Helpers helpers = new Helpers();



    private static final String LOG_TEMPLATE = "TransactionID={} | Transaction={}" +
            " | Process={} | ProcessDuration={}  | SourceSystem={} | TargetSystem={} " +
            "| ResponseCode={} | Request={} | RequestString={} | Response={} | ResponseString={}  | ResponseMsg={}";


    public Logging() {
        this.logger = LoggerFactory.getLogger(Logging.class);

    }



    @Async
    public void write() {

        switch (logLevel) {
            case "warn" ->
                    logger.warn(
                            LOG_TEMPLATE, transactionID,
                            transaction, process, processDuration, sourceSystem, targetSystem,
                            responseCode, shouldMaskPayload(request) ? helpers.maskSensitiveObject(request) : request ,
                            requestString !=null ? helpers.maskSensitiveString(requestString) : null,
                            shouldMaskPayload(response) ? helpers.maskSensitiveObject(response) : response,
                            responseString !=null ? helpers.maskSensitiveString(responseString) : null,
                            responseMsg
                    );

            case "error" ->
                    logger.error(
                            LOG_TEMPLATE, transactionID,
                            transaction, process, processDuration, sourceSystem, targetSystem,
                            responseCode, shouldMaskPayload(request) ? helpers.maskSensitiveObject(request) : request ,
                            requestString !=null ? helpers.maskSensitiveString(requestString) : null,
                            shouldMaskPayload(response) ? helpers.maskSensitiveObject(response) : response,
                            responseString !=null ? helpers.maskSensitiveString(responseString) : null,
                            responseMsg
                    );

            default ->
                    logger.info(
                            LOG_TEMPLATE, transactionID,
                            transaction, process, processDuration, sourceSystem, targetSystem,
                            responseCode, shouldMaskPayload(request) ? helpers.maskSensitiveObject(request) : request ,
                            requestString !=null ? helpers.maskSensitiveString(requestString) : null,
                            shouldMaskPayload(response) ? helpers.maskSensitiveObject(response) : response,
                            responseString !=null ? helpers.maskSensitiveString(responseString) : null,
                            responseMsg
                    );
        }
    }




    private boolean shouldMaskPayload(Object value) {
        // Condition for masking payload
        return value != null && !value.toString().isEmpty();
    }


}