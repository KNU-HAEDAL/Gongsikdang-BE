package com.food.config.jwt.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey SECRET_KEY;

    /**
     * 생성자: application.properties 파일에서 Secret Key를 주입받음
     */
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    /**
     * JWT 생성
     * @param userId 사용자 이름 (Payload의 Subject에 해당)
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId) // 사용자 id 설정
                .setIssuedAt(new Date()) // 토큰 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10시간 유효
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 서명 알고리즘과 Secret Key 사용
                .compact();
    }

    /**
     * JWT에서 Claims 추출
     * @param token JWT 토큰
     * @return Claims 객체
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * JWT에서 사용자 이름 추출
     * @param token JWT 토큰
     * @return 사용자 이름
     */
    public String extractUserId(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * JWT 만료 여부 확인
     * @param token JWT 토큰
     * @return 만료 여부 (true/false)
     */
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}



