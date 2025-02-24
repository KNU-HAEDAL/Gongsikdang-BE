package com.food.controller;

import com.food.dto.PurchaseDTO;
import com.food.service.PaymentService;
import com.food.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Purchase API", description = "구매 데이터 관련 API")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private PaymentService paymentService;

    /**
     * JWT 기반 구매 내역 조회 API
     */
    @Operation(summary = "구매 전체 내역 조회", description = "사용자의 JWT 토큰을 기반으로 구매 내역을 조회합니다.")
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @GetMapping("/purchases")
    public ResponseEntity<?> getPurchases(@AuthenticationPrincipal String userId) {
        List<PurchaseDTO> purchases = purchaseService.getPurchasesByUserId(userId);

        if (purchases.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("구매 내역이 없습니다.");
        }

        return ResponseEntity.ok(purchases);
    }

    /**
     * 결제 검증 및 구매 데이터 저장 API
     */
    @Operation(
            summary = "결제 검증 및 구매데이터 저장",
            description = "JWT 토큰과 결제 정보를 기반으로 결제 검증 후 구매 데이터를 저장합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "merchantUid : 고유 주문번호. 각 결제마다 달라야 합니다. (mid_ + 타임스탬프)\n" +
                                  "**포트원(아임포트)**의 **IMP.request_pay()**는 JavaScript SDK입니다.\n" +
                                  "Swagger UI는 HTTP 요청만 보낼 수 있으며, JavaScript 실행 환경이 아님.\n" +
                                  "따라서, Swagger에서는 결제창 호출이 불가능합니다.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"merchantUid\": \"mid_1234567890\",\n" +
                                            "  \"date\": \"2025-02-24T12:34:56.789Z\",\n" +
                                            "  \"totalAmount\": 20000,\n" +
                                            "  \"paymentMethod\": \"pay\",\n" +
                                            "  \"pgProvider\": \"kakao\",\n" +
                                            "  \"item\": [\n" +
                                            "    {\n" +
                                            "      \"foodId\": 1,\n" +
                                            "      \"foodName\": \"돈까스\",\n" +
                                            "      \"quantity\": 2,\n" +
                                            "      \"price\": 6000\n" +
                                            "    },\n" +
                                            "    {\n" +
                                            "      \"foodId\": 2,\n" +
                                            "      \"foodName\": \"떡볶이\",\n" +
                                            "      \"quantity\": 1,\n" +
                                            "      \"price\": 5500\n" +
                                            "    }\n" +
                                            "  ],\n" +
                                            "  \"status\": \"SUCCESS\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "결제 검증 완료 및 구매 데이터 저장 성공"),
                    @ApiResponse(responseCode = "400", description = "결제 검증 실패 또는 요청 데이터 오류")
            }
    )
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @PostMapping("/purchases")
    public ResponseEntity<?> savePurchase(@AuthenticationPrincipal String userId, @RequestBody PurchaseDTO purchaseDTO) {
        try {
            // 🔥 `merchant_uid`로 `imp_uid` 조회 (프론트는 `imp_uid`를 모름)
            String impUid = paymentService.getImpUidByMerchantUid(purchaseDTO.getMerchantUid());

            System.out.println("impUid조회성공");

            // 🔒 구매 내역 저장 (impUid 검증 및 트랜잭션 처리)
            purchaseService.savePurchase(purchaseDTO, userId, impUid);

            System.out.println("구매내역저장성공");

            return ResponseEntity.status(HttpStatus.CREATED).body("결제 검증 완료 및 구매 데이터 저장 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * 결제 취소 API (사용자가 직접 결제 취소)
     */
    @Operation(summary = "결제 취소", description = "사용자가 결제를 취소할 수 있습니다. -> 자동결제취소가 안될경우(네트워크 문제같은) 사용자가 직접 결제를 취소할 수 있어야 합니다")
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @PostMapping("/purchases/cancel")
    public ResponseEntity<?> cancelPayment(@RequestBody Map<String, String> requestBody) {
        String impUid = requestBody.get("imp_uid");
        String reason = requestBody.get("reason");

        boolean isCancelled = paymentService.cancelPayment(impUid, reason);
        if (isCancelled) {
            return ResponseEntity.ok("결제 취소 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 취소 실패");
        }
    }
}
