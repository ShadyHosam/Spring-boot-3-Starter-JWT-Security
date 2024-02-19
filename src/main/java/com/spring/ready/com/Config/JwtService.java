package com.spring.ready.com.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.*;

@Service
public class JwtService {

    private static final String SECRETKEY = "YourSecretKey";

    public String extractUsername(String token){
        return extractClaims(token,Claims::getSubject);
    }
    public Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);
    }
    public <T> T extractClaims(String token , Function<Claims,T> claimResolver){
        final Claims claims= extractAllClaims(token);
        return claimResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token)
                .before(new Date());
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(userDetails, new HashMap<>());
    }
    public String generateToken(UserDetails userDetails , Map<String ,Object> extraClaims){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*10))
                .signWith(getSignInKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token , UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRETKEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
