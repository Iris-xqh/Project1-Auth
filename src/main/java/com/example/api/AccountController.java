package com.example.api;

import com.example.domain.Customer;
import com.example.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    TokenService tokenService;
    @GetMapping("/token")
    public String generateJWT(@RequestParam String name, @RequestParam String password) {
        return tokenService.generateTokenForClient(name, password);
    }

    @GetMapping("/test")
    public String test2() {
        return "API works";
    }
}
