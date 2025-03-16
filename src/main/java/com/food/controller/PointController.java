package com.food.controller;

import com.food.service.PaymentService;
import com.food.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
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
     * 🔥 포인트 충전 API (포트원 결제 검증 포함)
     */
    @Operation(
            summary = "포인트 충전 API",
            description = "사용자의 포인트를 충전합니다. (포트원 결제 검증 포함)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "**포트원(아임포트)**의 **IMP.request_pay()**는 JavaScript SDK입니다.\n" +
                            "프론트에서 결제 성공 후, `imp_uid`와 `merchant_uid`를 함께 백엔드로 전달해야 합니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"impUid\": \"imp_1234567890\", \"money\": 10000 }")
                    )
            )
    )
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @PostMapping("/charge")
    public ResponseEntity<String> chargePoint(
            @AuthenticationPrincipal String userId,
            @RequestBody Map<String, Object> requestBody
    ) {
        try {
            int money = (int) requestBody.get("money");
            String impUid = (String) requestBody.get("impUid"); // ✅ 프론트에서 `impUid`를 직접 받음

            // 🔒 포인트 저장 (impUid 검증 및 트랜잭션 처리)
            pointService.savePoint(userId, money, impUid);
            return ResponseEntity.ok("포인트 충전 완료");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("포인트 충전 실패: " + e.getMessage());
        }
    }

    /**
     * 🔥 포인트 조회 API
     */
    @Operation(summary = "포인트 조회 API", description = "사용자의 포인트를 조회합니다.")
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @GetMapping
    public ResponseEntity<Integer> getPoint(@AuthenticationPrincipal String userId) {
        try {
            int point = pointService.getUserPoint(userId);
            return ResponseEntity.ok(point);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(-1); // 오류 시 -1 반환
        }
    }
}
