package com.shop.util;

import com.shop.domain.model.TokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {
    private long TIME_MIN = 1000 * 60;
    private long TIME_ACCESS_TOKEN_EXPIRED_TIME = TIME_MIN * 2;
    private long TIME_REFRESH_TOKEN_EXPIRED_TIME = TIME_MIN * 10;
    private String SECRET = "TEST1234123123|TEST1234123123|1234123";
    private Key SECRET_KEY;

    private JWTUtil() {
        this.SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public TokenInfo createToken(String email) {
        // JWT 토근 생성
        String accessToken = Jwts.builder()
                .setSubject("AccessToken")
                .setExpiration(new Date(System.currentTimeMillis() + TIME_ACCESS_TOKEN_EXPIRED_TIME))
                .claim("email", email)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject("RefreshToken")
                .setExpiration(new Date(System.currentTimeMillis() + TIME_ACCESS_TOKEN_EXPIRED_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshExpiredTime(TIME_REFRESH_TOKEN_EXPIRED_TIME)
                .email(email)
                .build();
    }

    public String getAccessTokenToEmail(String token) {
        Claims claims = getClaims(token);
        return claims.get("email").toString();
    }

    public String getAccessTokenToExpiredEmail(String token) {
        Claims claims;
        try {
            claims = getClaims(token);
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        } catch (Exception e) {
            return null;
        }
        return claims.get("email").toString();
    }

    private Claims getClaims(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(accessToken).getBody();
    }

    public Long getExpired(String accessToken) {
        Date date = Jwts.parserBuilder().setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getExpiration();
        return (date.getTime() - (new Date().getTime()));
    }
}

