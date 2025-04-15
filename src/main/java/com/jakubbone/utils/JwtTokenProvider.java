package com.jakubbone.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Getter
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.jwt.expiration}")
    private long expirationMillis;

    public String createToken(String username, String role){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() - expirationMillis);

        return Jwts.builder()
                .setSubject(username)         // set token subject (username)
                .claim("role", role)       // own field (role)
                .setIssuedAt(now)             // release time
                .setExpiration(expiryDate)    // expiration time
                .signWith(SignatureAlgorithm.HS512, secretKey)  // sign with HS512 algorithm and secret
                .compact();
    }
}
