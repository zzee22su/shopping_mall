package com.shop.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ResponseData {
    public LocalDateTime timestamp;
    public int status;
    public Object data;
    public String error;
    public String code;
    public String message;
}
