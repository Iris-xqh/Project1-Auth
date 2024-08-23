package com.example.service;

import com.example.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RegisterService {
    String authServerToken;
    @Autowired
    TokenService tokenService;

    public ResponseEntity<?> register(Customer customer) {
        // use RestTemplate to make a POST request to the Customer API
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://cusapp:8080/api/customers";

        // add the access token to the request headers
        String accessToken = "Bearer " + tokenService.generateToken("authServer");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION,  accessToken);

        // create an HttpEntity object with the customer object and headers
        HttpEntity<Customer> httpEntity = new HttpEntity<>(customer, httpHeaders);

        try{
            // make the POST request
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
            return responseEntity;
        }catch (Exception e) {
            System.out.println("POST API Error: " + e.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }

}
