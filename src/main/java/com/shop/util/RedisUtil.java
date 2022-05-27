package com.shop.util;

import com.shop.domain.model.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate redisTemplate;

    public void saveRedisRefreshToken(TokenInfo tokenInfo) {
        redisTemplate.opsForValue()
                .set("RT:" + tokenInfo.getEmail(),
                        tokenInfo.getRefreshToken(),
                        tokenInfo.getRefreshExpiredTime(),
                        TimeUnit.MILLISECONDS);
    }

    public String getRedisRefreshToken(String email) {
        return (String) redisTemplate.opsForValue().get("RT:" + email);
    }

    public void saveLogOutToken(String accessToken, Long expiredDate) {
        redisTemplate.opsForValue()
                .set(accessToken,
                        "value",
                        expiredDate,
                        TimeUnit.MILLISECONDS);
    }

    public boolean isLogOutToken(String accessToken) {
        String token = (String) redisTemplate.opsForValue().get(accessToken);
        if (token == null) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteRefreshToken(String email) {
        redisTemplate.delete("RT:" + email);
    }
}
