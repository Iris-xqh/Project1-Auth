package com.example.api;

import com.example.domain.Customer;
import com.example.service.RegisterService;
import com.example.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// handles the API endpoints for token generation and registration
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    TokenService tokenService;
    @Autowired
    RegisterService registerService;

    // generates a JWT token for the client
    @GetMapping("/token")
    public String generateJWT(@RequestParam("name") String name, @RequestParam("password") String password) {
        return tokenService.generateTokenForClient(name, password);
    }

    // registers a new customer
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Customer customer) {
        return registerService.register(customer);
    }

    @GetMapping
    public String test() {
        return "API works";
    }
}
