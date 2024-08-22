package com.example.api;

import com.example.domain.Customer;
import com.example.service.RegisterService;
import com.example.service.TokenService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    TokenService tokenService;
    @Autowired
    RegisterService registerService;

    @GetMapping("/token")
    public String generateJWT(@RequestParam String name, @RequestParam String password) {
        return tokenService.generateTokenForClient(name, password);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Customer customer) {
        return registerService.register(customer);
    }

    @GetMapping
    public String test() {
        return "API works";
    }
}
