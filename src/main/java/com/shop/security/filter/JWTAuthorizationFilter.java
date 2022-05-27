package com.shop.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.domain.request.User;
import com.shop.response.Response;
import com.shop.security.CustomUserDetail;
import com.shop.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
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
                    setForbiddenResponse("인증에 실패하였습니다.", response);
                }
            }
        } catch (Exception e) {
            if (e instanceof ExpiredJwtException) {
                setForbiddenResponse(e.getMessage(), response);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private void setForbiddenResponse(String msg, HttpServletResponse response) throws IOException {
        Response resultResponse = new Response();
        resultResponse.setErrorResponse(msg, HttpStatus.FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        new ObjectMapper().writeValue(response.getOutputStream(), resultResponse);
    }
}
