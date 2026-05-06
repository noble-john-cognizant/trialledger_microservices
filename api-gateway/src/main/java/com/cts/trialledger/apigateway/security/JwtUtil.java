package com.cts.trialledger.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtUtil {

    private final static String SECRET = "fkjsad3433545jkfKJFDFSN745$%^fdw";
    public final static Integer EXPIRATION_TIME = 15 * 60;

    public String generateToken(Long userId, String name, String email, String role) {
        Map<String, String> payload = new HashMap<>();
        payload.put("role", role);
        payload.put("name", name);
        payload.put("id", userId.toString());
        return Jwts.builder()
                .claims(payload)
                .subject(email)
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(EXPIRATION_TIME)))
                .signWith(getSecretKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public boolean validateToken(String token) {
        return parseToken(token).getExpiration().after(new Date());
    }
}
