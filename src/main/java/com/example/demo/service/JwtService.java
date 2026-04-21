package com.example.demo.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final String secretKey = "mysecretkeymysecretkeymysecretkey"; // In production, use a secure key and store it safely

    public String generateToken(String username) {
        // Implement JWT token generation logic here
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 20  )) // token valid for 20 minutes
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                // sign the token with the secret key using HMAC SHA-256 algorithm
                .compact();// Generate the JWT token and return it as a string

    }

    public String extractUsername(String token) {
        // Parse the token and extract the subject (username)
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes()); // Create a SecretKey from the secret string

       try {
           return Jwts.parser()             // Now returns a builder directly
                   .verifyWith(key)         // Replaces setSigningKey()
                   .build()
                   .parseSignedClaims(token)// Replaces parseClaimsJws()
                   .getPayload()            // Replaces getBody()
                   .getSubject();
         } catch (Exception e) {
           // Handle token parsing exceptions (e.g., expired, invalid)
           return null;
       }
    }
}
