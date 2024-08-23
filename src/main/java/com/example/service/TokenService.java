package com.example.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.domain.Customer;
import com.google.gson.Gson;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class TokenService {
    private String authServerToken;

    public String generateTokenForClient(String username, String password) {
        //check this user exists through Custermer API
        //if exists, generate token
        if (username != null && password != null && checkExist(username, password)) {
            return generateToken(username);
        }
        return "Invalid username or password";
    }

    //get customer by calling Customer API
    public Customer getCustomerFromCustomerAPI(String username) {
        String customerStr = "";
        RestTemplate restTemplate = new RestTemplate();
        // String url = "http://localhost:8080/api/customers/getbyname/" + username;
        //cusapp is the name of the service in docker network
        String url = "http://cusapp:8080/api/customers/getbyname/" + username;

        // add the access token to the request headers
        String accessToken = "Bearer " + generateToken("authServer");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, accessToken);
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);

        // create a ParameterizedTypeReference object to specify the response type
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {
        };
        try {
            // make the GET request
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, responseType);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                customerStr = responseEntity.getBody();
            }
        } catch (Exception e) {
            System.out.println("GET API Error: " + e.getMessage());
        }

        //parse the response to a Customer object
        Gson gson = new Gson();
        Customer customer = gson.fromJson(customerStr, Customer.class);
        return customer;
    }

    //check if the user exists
    private boolean checkExist(String username, String password) {
        Customer customer = getCustomerFromCustomerAPI(username);
        if (customer == null) {
            return false;
        }
        if (!customer.getPassword().equals(password)) {
            return false;
        }
        return true;
    }

    //generate token
    public String generateToken(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null when generating token");
        }

        // create algorithm containing the secret key
        Algorithm algorithm = Algorithm.HMAC256("secret"); // May need to change this secret to a more secure one

        // create the token
        String generatedToken = JWT.create()
                .withIssuer("auth0")
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(new Date().toInstant().plusSeconds(7200))) // 2 hour expiration
                .withClaim("name", username == "authServer" ? "authServer" : "client") // Set the name claim to "authServer" if the username is "authServer"
                .sign(algorithm);

        // there is no need to generate a new token for the authServer if it already has one
        if (username.equals("authServer") && authServerToken == null) {
            authServerToken = generatedToken;
            return generatedToken;
        }
        return generatedToken;
    }
}
