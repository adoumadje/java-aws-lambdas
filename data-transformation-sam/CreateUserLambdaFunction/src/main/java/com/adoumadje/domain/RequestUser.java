package com.adoumadje.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RequestUser {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPass;
}
