package com.he180773.testreact.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    private static final String SECRET_KEY = "my-secret-key-should-be-very-long-and-secure"; // Thay bằng khóa bí mật thực tế
    private static final long EXPIRATION_TIME = 86400000; // 1 ngày

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // Tạo JWT token
    public String generateToken(String username, String role) {
        String token= Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        System.out.println(token);
        return token;
    }

    // Lấy username từ token
    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Lấy role từ token
    public String extractRole(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    // Hàm kiểm tra Token hết hạn hay chưa
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    // Hàm lấy ngày hết hạn từ Token
    public Date extractExpiration(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getExpiration();
    }

    // Hàm lấy toàn bộ claims từ token
    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }



}
