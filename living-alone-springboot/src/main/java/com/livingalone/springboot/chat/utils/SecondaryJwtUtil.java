package com.livingalone.springboot.chat.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class SecondaryJwtUtil {

    public static String createJwt(Long userId, Key key, Long expiredMs) {
        String jwt = Jwts.builder()
                .claim("userId", userId)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(new Date(new Date().getTime() + expiredMs))
                .compact();
        return jwt;
    }

    public static Claims getClaimsFromToken(Key key, String jwt) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        Date expiration = claims.getExpiration();
        if(expiration.before(new Date())) {
            // 예외처리 필요
            return null;
        }
        return claims;
    }

    public static Long getUserIdFromClaims(Claims claims) {
        return claims.get("userId", Long.class);
    }
}
