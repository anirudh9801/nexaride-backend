package com.nexaride.user.service;

import com.nexaride.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    //Get key
    @Getter
    private SecretKey key;

    //initialize key after injection
    @PostConstruct
    public void init(){
        key = Keys.hmacShaKeyFor(secret.getBytes());
        System.out.println("JWT Secret key Loaded successfully");
    }

    //Generate token
    public String generateToken(User user){
        log.debug("Inside generateToken method..");
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role",user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //Extract Username
    public String extractUsername(String token){
        log.info("Extracting username: "+getClaims(token).getSubject());
        return getClaims(token).getSubject();
    }

    //Validate token
    public boolean validateToken(String token,String email){
        log.info("Validating the token in the method validate token");
        final String username = extractUsername(token);
        return username.equals(email) && !isTokenExpired(token);
    }
    //Check Expiry
    private boolean isTokenExpired(String token){
        return getClaims(token).getExpiration().before(new Date());
    }
    //Extarct All Claims
    public Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
