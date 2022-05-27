package com.shop.domain.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TokenInfo {
    private Long refreshExpiredTime;
    private String refreshToken;
    private String accessToken;
    private String email;
}
