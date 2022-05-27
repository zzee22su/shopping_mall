package com.shop.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class Response {
    private String status;
    private int statusCode;
    private Object data;
    private String message;

    public Response() {
        init();
    }

    public Response(String message) {
        this.message = message;
        init();
    }

    public void setResponse(String message, Object value) {
        this.message = message;
        setData(value);
    }

    private void init(){
        status = HttpStatus.OK.getReasonPhrase();
        statusCode = HttpStatus.OK.value();
    }

    public void setResponse(String message, Object value, String status, int statusCode) {
        this.message = message;
        this.status = status;
        this.statusCode = statusCode;
        setData(value);
    }

    public void setErrorResponse(String message, HttpStatus httpStatus) {
        this.status = httpStatus.getReasonPhrase();
        this.statusCode = httpStatus.value();
        this.message = message;
    }


    private void setData(Object value) {
        if (data instanceof List) {
            ((List<Object>) data).add(value);
        } else {
            data = value;
        }
    }
}
