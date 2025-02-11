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
     * @param userId 사용자 ID (Payload의 Subject에 해당)
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId) // 사용자 ID 설정
                .setIssuer("Gongsikdang") // 애플리케이션 이름 추가
                .setIssuedAt(new Date()) // 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10시간 유효
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 서명 알고리즘 및 키 설정
                .compact();
    }

    /**
     * JWT에서 Claims 추출
     * @param token JWT 토큰
     * @return Claims 객체
     */
    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.out.println("❌ JWT 토큰 파싱 실패: " + e.getMessage());
            return null;
        }
    }

    /**
     * JWT에서 사용자 ID 추출
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public String extractUserId(String token) {
        if (token == null || token.isEmpty()) {
            System.out.println("❌ JWT 토큰이 비어있음");
            return null;
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = extractClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * JWT 만료 여부 확인
     * @param token JWT 토큰
     * @return 만료 여부 (true/false)
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims != null && claims.getExpiration().before(new Date());
        } catch (Exception e) {
            System.out.println("❌ JWT 토큰 유효성 검사 실패: " + e.getMessage());
            return true; // 오류 발생 시 만료된 것으로 처리
        }
    }
}
