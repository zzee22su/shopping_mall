package com.shop.response;

import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.OK;

@Data
public class Response {
    public ResponseEntity<ResponseData> createErrorResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseData.builder()
                        .timestamp(LocalDateTime.now())
                        .status(errorCode.getHttpStatus().value())
                        .error(errorCode.getHttpStatus().name())
                        .code(errorCode.name())
                        .message(errorCode.getDetail())
                        .build()
                );
    }

    public ResponseData createErrorResponseData(ErrorCode errorCode) {
        return ResponseData.builder()
                .status(errorCode.getHttpStatus().value())
                .error(errorCode.getHttpStatus().name())
                .code(errorCode.name())
                .message(errorCode.getDetail())
                .build();
    }

    public ResponseData createResponseData(String msg, Object object) {
        return ResponseData.builder()
                .status(OK.value())
                .data(object)
                .code(OK.name())
                .message(msg)
                .build();
    }

    public ResponseEntity<ResponseData> createResponseEntity(String msg, Object object) {
        return ResponseEntity
                .ok()
                .body(ResponseData.builder()
                        .status(OK.value())
                        .data(object)
                        .code(OK.name())
                        .message(msg)
                        .build()
                );
    }

    public static Response getNewInstance() {
        return new Response();
    }
}
