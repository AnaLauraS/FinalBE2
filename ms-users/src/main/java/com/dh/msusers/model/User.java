package com.dh.msusers.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class User {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private List<Bill> bills;

    public User(String id, String username, String email, String firstName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
    }

    public User(String id, String username, String email, String firstName, List<Bill> bills) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.bills = bills;
    }
}
