package com.shop.controller;

import com.shop.domain.request.User;
import com.shop.response.ResponseData;
import com.shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

    @PostMapping("/sign")
    public ResponseEntity<ResponseData> join(@RequestBody User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        return userService.createUser(user);
    }

    @PostMapping("/token/refresh")
    //public String join(HttpServletRequest request, @RequestBody String parameter){
    //public String refreshToken(HttpServletRequest request, @RequestParam("refresh_token") String refreshToken){
    public ResponseEntity<ResponseData> refreshToken(HttpServletRequest request) {
        //System.out.println("1parameter"+refreshToken+"\r\n");
        //System.out.println("2parameter"+request.getParameter("refresh_token")+"\r\n");
        String refreshToken = request.getParameter("refreshToken");
        String accessToken = request.getParameter("accessToken");
        return userService.refreshToken(refreshToken, accessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseData> logout(HttpServletRequest request) {
        return userService.logout(request.getParameter("accessToken"));
    }

    @GetMapping("/user")
    public ResponseEntity<ResponseData> user(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION).replace("Bearer ", "");
        return userService.getUserInfo(token);
    }

    @GetMapping("/sign")
    public ResponseEntity<ResponseData> signedUser(HttpServletRequest request) {
        String email=request.getParameter("email");
        return userService.checkSignedEmail(email);
    }
}
