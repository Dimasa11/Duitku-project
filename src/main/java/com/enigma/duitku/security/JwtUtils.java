package com.enigma.duitku.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtUtils {
    // fungsi JwtUtils utils untuk secret dan expired token

    @Value("ini rahasia lah kocak")
    private String jwtSecret;

    @Value("8000000") // setiap 3 menit atau 3 juta milis
    private Long jwtExpiration;

    public String getMobileNumberByToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public String extractUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public String generateToken(String mobileNumber) {
        return Jwts.builder()
                .setSubject(mobileNumber)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .compact();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT Token {} ", e.getMessage());
        }catch (ExpiredJwtException e) {
            log.error("JWT token is expired {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported {}", e.getMessage());
        }catch (IllegalArgumentException e) {
            log.error("JWT claims String is empty {}", e.getMessage());
        }
        return false;
    }

}
