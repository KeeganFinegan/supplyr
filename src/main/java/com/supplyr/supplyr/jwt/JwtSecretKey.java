package com.supplyr.supplyr.jwt;


import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JwtSecretKey {


    @Autowired
    JwtConfiguration jwtConfiguration;

    @Bean
    public SecretKey secretKey(){
        return Keys.hmacShaKeyFor(jwtConfiguration.getSecretKey().getBytes());
    }

}
