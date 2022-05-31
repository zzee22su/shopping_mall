package com.shop.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.domain.model.TokenInfo;
import com.shop.domain.request.User;
import com.shop.response.Response;
import com.shop.security.CustomUserDetail;
import com.shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.shop.response.ErrorCode.INVALID_EMAIL_PASSWORD;
import static com.shop.response.ErrorCode.INVALID_UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

// 로그인 인증
// 시큐리티 스프링을 동작 위해서 만든 SecurityConfig의 configure(HttpSecurity http) 콜백 함수에 http로 .add filter 등록하기 위한 필터
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private UserService userService;
    private final AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, UserService userService) {
        super(authenticationManager);
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    // /login 요청을 하면 로그인 시도를 위해서 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (request.getInputStream().available() == 0) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                new ObjectMapper().writeValue(response.getOutputStream(),
                        Response.getNewInstance().createErrorResponseData(INVALID_EMAIL_PASSWORD));
                return null;
            }
            User user = objectMapper.readValue(request.getInputStream(), User.class);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            return authentication;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(UNAUTHORIZED.value());
        new ObjectMapper().writeValue(response.getOutputStream(), Response.getNewInstance()
                .createErrorResponseData(INVALID_UNAUTHORIZED));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("로그인 성공 시 호출 되는 콜백 successfulAuthentication");

        // authResult에 UserDetails의 정보를 가지고 있다.
        CustomUserDetail customUserDetail = (CustomUserDetail) authResult.getPrincipal();

        TokenInfo tokenInfo = userService.createToken(customUserDetail.getUsername());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("refreshToken", "Bearer " + tokenInfo.getRefreshToken());
        tokens.put("accessToken", "Bearer " + tokenInfo.getAccessToken());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        userService.saveRedisRefreshToken(tokenInfo);
        new ObjectMapper().writeValue(response.getOutputStream(),
                Response.getNewInstance().createResponseData("로그인 성공",tokens));
    }
}