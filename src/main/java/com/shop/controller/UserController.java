package com.shop.controller;

import com.shop.domain.request.User;
import com.shop.response.Response;
import com.shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

    @PostMapping("/api/v1/sign")
    public String join(@RequestBody User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        int error = userService.createUser(user);
        System.out.println("error" + error);
        return "";
    }

    @PostMapping("/api/v1/token/refresh")
    //public String join(HttpServletRequest request, @RequestBody String parameter){
    //public String refreshToken(HttpServletRequest request, @RequestParam("refresh_token") String refreshToken){
    public Response refreshToken(HttpServletRequest request) {
        //System.out.println("1parameter"+refreshToken+"\r\n");
        //System.out.println("2parameter"+request.getParameter("refresh_token")+"\r\n");
        String refreshToken = request.getParameter("refreshToken");
        String accessToken = request.getParameter("accessToken");
        return userService.refreshToken(refreshToken, accessToken);
    }

    @PostMapping("/api/v1/logout")
    public Response logout(HttpServletRequest request) {
        return userService.logout(request.getParameter("accessToken"));
    }

    @GetMapping("/api/v1/user")
    public Response user(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION).replace("Bearer ", "");
        return userService.getUserInfo(token);
    }
}
