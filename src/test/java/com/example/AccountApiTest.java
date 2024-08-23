package com.example;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class AccountApiTest {
    @Autowired
    MockMvc mvc;

    private String defaultUsername = "authServer";
    private String defaultPassword = "defaultPassword";
    private Algorithm algorithm = Algorithm.HMAC256("secret");

    // Test to check if the API works without valid username and passsword
    // The test should return an internal server error
    @Test
    void testWithoutValidInfo() throws Exception {
        String result = mvc.perform(get("/account/token")
                .param("name", "invalidUsername")
                .param("password", "invalidPassword"))
                .andReturn().getResponse().getContentAsString();
        assertEquals(result, "Invalid username or password");
    }

    // Test to check if the API works with valid username and passsword
    // The test should return an OK status and a JWT token
    @Test
    void testWithValidInfo() throws Exception {
        String result = mvc.perform(get("/account/token")
                .param("name", defaultUsername)
                .param("password", defaultPassword))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build();
        DecodedJWT jwt = verifier.verify(result);
        assertEquals(jwt.getClaim("name").asString(), defaultUsername);
    }

}
