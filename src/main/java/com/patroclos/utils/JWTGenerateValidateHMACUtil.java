package com.patroclos.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JWTGenerateValidateHMACUtil {

//    public void main(String[] args) {
//
//        String jwt = createJwtSignedHMAC();
//
//        Jws<Claims> token = parseJwt(jwt);
//
//        System.out.println(token.getBody());
//    }
//
//    public Jws<Claims> parseJwt(String jwtString) {
//
//        String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";
//
//        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.getJcaName());
//
//        Jws<Claims> jwt = Jwts.parserBuilder()
//                .setSigningKey(hmacKey)
//                .build()
//                .parseClaimsJws(jwtString);
//
//        return jwt;
//    }
//
//
//    public String createJwtSignedHMAC() {
//
//        String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";
//
//        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.getJcaName());
//
//        Instant now = Instant.now();
//        String jwtToken = Jwts.builder()
//                .claim("name", "Jane Doe")
//                .claim("email", "jane@example.com")
//                .setSubject("jane")
//                .setId(UUID.randomUUID().toString())
//                .setIssuedAt(Date.from(now))
//                .setExpiration(Date.from(now.plus(5l, ChronoUnit.MINUTES)))
//                .signWith(hmacKey)
//                .compact();
//
//        return jwtToken;
//    }

}