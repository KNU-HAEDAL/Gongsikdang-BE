package com.food.controller;

import com.food.config.jwt.token.JwtUtil;
import com.food.service.PaymentService;
import com.food.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/point")
@Tag(name = "Point API", description = "포인트 관련 API")
public class PointController {

    @Autowired
    private PointService pointService;

    @Autowired
    private PaymentService paymentService;

    /**
     * 포인트 충전 API (포트원 결제 검증 포함)
     */
    @Operation(
            summary = "포인트 충전",
            description = "사용자의 포인트를 충전합니다.(포인트 검증 및 저장)"
    )
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @PostMapping
    public ResponseEntity<String> chargePoint(
            @AuthenticationPrincipal String userId,
            @RequestBody Map<String, Object> requestBody
    ) {
        try {
            int money = (int) requestBody.get("money");
            String merchantUid = (String) requestBody.get("merchant_uid"); // 주문 번호

            // 🔥 `merchant_uid`로 `imp_uid` 조회 (프론트는 `imp_uid`를 모름)
            String impUid = paymentService.getImpUidByMerchantUid(merchantUid);

            // 🔒 포인트 저장 (impUid 검증 및 트랜잭션 처리)
            pointService.savePoint(userId, money, impUid);
            return ResponseEntity.ok("포인트 충전 완료");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid Token or Payment Verification Failed");
        }
    }

    /**
     * 포인트 조회 API
     */
    @Operation(
            summary = "포인트 조회",
            description = "사용자의 포인트를 조회합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @GetMapping
    public ResponseEntity<Integer> getPoint(@AuthenticationPrincipal String userId) {
        try {
            int point = pointService.getUserPoint(userId);
            return ResponseEntity.ok(point);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(-1); // 오류 시 -1 반환
        }
    }
}