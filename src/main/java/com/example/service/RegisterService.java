package com.example.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.domain.Customer;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class RegisterService {
    String authServerToken;
    @Autowired
    TokenService tokenService;

    public ResponseEntity<?> register(Customer customer) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://cusapp:8080/api/customers";
        String accessToken = "Bearer " + tokenService.generateToken("authServer");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION,  accessToken);
        HttpEntity<Customer> httpEntity = new HttpEntity<>(customer, httpHeaders);
//        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {
//        };
        try{
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
            return responseEntity;
        }catch (Exception e) {
            System.out.println("POST API Error: " + e.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }

}
