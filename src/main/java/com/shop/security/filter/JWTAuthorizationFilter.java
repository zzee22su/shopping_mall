package com.shop.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.domain.request.User;
import com.shop.response.Response;
import com.shop.response.ResponseData;
import com.shop.security.CustomUserDetail;
import com.shop.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.shop.response.ErrorCode.*;

//허가
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private UserService userService;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, UserService userService) {
        super(authenticationManager);
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = userService.getHeaderAuthorization(request);
        try {
            if (token != null && !userService.isLogOutToken(token)) {
                String email = userService.getAccessTokenToEmail(token);
                if (email != null) {
                    User user = userService.getFindByEmail(email);
                    CustomUserDetail customUserDetail = new CustomUserDetail(user);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetail, null, customUserDetail.getAuthorities());
                    // 사용자 정보를 가져다가 강제로 세션에 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    chain.doFilter(request, response);
                    return;
                } else {
                    setResponseError(response,Response
                            .getNewInstance()
                            .createErrorResponseData(INVALID_UNAUTHORIZED));
                    return;
                }
            }
        } catch (Exception e) {
            if (e instanceof ExpiredJwtException) {
                setResponseError(response, Response
                        .getNewInstance()
                        .createErrorResponseData(INVALID_EXPIRED_TOKEN));
            } else {
                setResponseError(response, Response
                        .getNewInstance()
                        .createErrorResponseData(INVALID_TOKEN));
            }

        }
        chain.doFilter(request, response);
    }

    private void setResponseError(HttpServletResponse response, ResponseData responseData) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(responseData.getStatus());
        new ObjectMapper().writeValue(response.getOutputStream(), responseData);
        ;
    }
}
