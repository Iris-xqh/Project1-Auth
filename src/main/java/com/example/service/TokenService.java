package com.example.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.domain.Customer;
//import com.nimbusds.jose.shaded.gson.Gson;
import com.google.gson.Gson;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import java.util.Date;

@Service
public class TokenService {
    private String authServerToken;
    public String generateTokenForClient(String username, String password) {
        //check this user exists through Custermer API
        if(username != null && password != null && checkExist(username, password)) {
            return generateToken(username);
        }
        return "Invalid username or password";
    }

    public Customer getCustomerFromCustomerAPI(String username) {
        String customerStr = "";
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/api/customers/getbyname/" + username;
        String accessToken = "Bearer " + generateToken("authServer");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION,  accessToken);
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {
        };
        try{
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, responseType);
            if(responseEntity.getStatusCode().is2xxSuccessful()) {
                customerStr = responseEntity.getBody();
            }
        }catch (Exception e) {
            System.out.println("GET API Error: " + e.getMessage());
        }

        //String customerStr = restTemplate.getForObject(url, String.class);
//       JSONObject customerJsonObject = new JSONObject(customerStr);
        Gson gson = new Gson();
        Customer customer = gson.fromJson(customerStr, Customer.class);
        return customer;
    }

    private boolean checkExist(String username, String password) {
        Customer customer = getCustomerFromCustomerAPI(username);
        if(customer == null) {
            return false;
        }
        if(!customer.getPassword().equals(password)) {
            return false;
        }
        return true;
    }

    public String generateToken(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null when generating token");
        }
        Algorithm algorithm = Algorithm.HMAC256("secret"); // May need to change this secret to a more secure one
        String generatedToken = JWT.create()
                .withIssuer("auth0")
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(new Date().toInstant().plusSeconds(7200))) // 2 hour expiration
                .withClaim("name", username == "authServer" ? "authServer" : "client")
                .sign(algorithm);

        if(username.equals("authServer") && authServerToken == null) {
            authServerToken = generatedToken;
            return generatedToken;
        }
        return generatedToken;
    }
}
