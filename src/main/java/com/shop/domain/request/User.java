package com.shop.domain.request;

import lombok.Data;

@Data
public class User {
    private long id;
    private String email;
    private String password;
    private String role;
    private String name;
    private String phoneNumber;
    private String address;
}