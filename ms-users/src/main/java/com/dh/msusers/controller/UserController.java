package com.dh.msusers.controller;

import com.dh.msusers.model.User;
import com.dh.msusers.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/findBy")
    public ResponseEntity<User> getAll(@RequestParam String customerBill) {
        return ResponseEntity.ok().body(service.getAllBill(customerBill));
    }
}
