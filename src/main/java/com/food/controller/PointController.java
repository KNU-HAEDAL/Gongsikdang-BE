package com.food.controller;

import com.food.config.jwt.token.JwtUtil;
import com.food.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/point")
@Tag(name = "Point API", description = "포인트 관련 API")
public class PointController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PointService pointService;

    /**
     * 포인트 충전 API (포트원 결제 검증 포함)
     */
    @Operation(summary = "포인트 충전", description = "사용자의 포인트를 충전합니다.")
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @PostMapping
    public ResponseEntity<String> chargePoint(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> requestBody
    ) {
        try {
            String userId = jwtUtil.extractUserId(token);
            int money = (int) requestBody.get("money");
            String impUid = (String) requestBody.get("imp_uid");

            pointService.savePoint(userId, money, impUid);
            return ResponseEntity.ok("포인트 충전 완료");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid Token or Payment Verification Failed");
        }
    }

    /**
     * 포인트 조회 API
     */
    @Operation(summary = "포인트 조회", description = "사용자의 포인트를 조회합니다.")
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @GetMapping
    public ResponseEntity<Integer> getPoint(@RequestHeader("Authorization") String token) {
        try {
            String userId = jwtUtil.extractUserId(token);
            int point = pointService.getUserPoint(userId);
            return ResponseEntity.ok(point);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(-1); // 오류 시 -1 반환
        }
    }
}