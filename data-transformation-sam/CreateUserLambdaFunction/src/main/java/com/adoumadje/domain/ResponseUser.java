package com.adoumadje.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class ResponseUser {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
}
