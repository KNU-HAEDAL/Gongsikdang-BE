package com.food.config.jwt.filter;

import com.food.config.jwt.token.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Swagger 경로를 필터링에서 제외
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        boolean shouldFilter = !(path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/user/register")
                || path.startsWith("/user/login"));

        System.out.println("🔥 JWT 필터 적용됨? " + shouldFilter + " (요청: " + path + ")");
        return !shouldFilter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;
        String userId = null;

        try {
            // Authorization 헤더에서 Bearer 토큰 추출
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7); // "Bearer " 이후의 토큰 값
                userId = jwtUtil.extractUserId(jwt); // 사용자 id 추출
            }

            // SecurityContext에 인증 정보가 없는 경우
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (!jwtUtil.isTokenExpired(jwt)) { // 토큰이 만료되지 않았을 경우
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, new ArrayList<>()); // 권한 정보는 빈 리스트로 설정
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication); // 인증 정보 설정
                } else {
                    // 토큰 만료된 경우 예외 발생
                    throw new ExpiredJwtException(null, null, "JWT token is expired");
                }
            }
        } catch (ExpiredJwtException e) {
            handleException(response, "Expired JWT token", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (MalformedJwtException e) {
            handleException(response, "Invalid JWT token", HttpServletResponse.SC_BAD_REQUEST);
            return;
        } catch (UnsupportedJwtException e) {
            handleException(response, "Unsupported JWT token", HttpServletResponse.SC_BAD_REQUEST);
            return;
        } catch (SignatureException e) {
            handleException(response, "Invalid JWT signature", HttpServletResponse.SC_BAD_REQUEST);
            return;
        } catch (IllegalArgumentException e) {
            handleException(response, "JWT claims string is empty", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        chain.doFilter(request, response); // 다음 필터로 요청 전달
    }

    /**
     * 예외 발생 시 HTTP 응답 설정
     *
     * @param response HttpServletResponse 객체
     * @param message  에러 메시지
     * @param status   HTTP 상태 코드
     */
    private void handleException(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
    }
}



