package com.adoumadje.photoapp.users.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class User {
    private UUID id;
    private String firstName;
    private String lastName;
}
