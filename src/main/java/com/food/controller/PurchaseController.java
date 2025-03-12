package com.food.controller;

import com.food.dto.PurchaseDTO;
import com.food.service.PaymentService;
import com.food.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
    @Operation(
            summary = "구매 내역 조회 API",
            description = "사용자의 구매 내역을 조회합니다. 구매 내역이 없으면 빈 배열([])을 반환합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = {
                                    @Content(mediaType = "application/json",
                                            examples = {
                                                    @ExampleObject(
                                                            name = "구매 내역 있음",
                                                            value = "[{\"purchaseId\": 1, \"merchantUid\": \"123456\", \"userId\": \"hello\", \"totalAmount\": 6000, \"paymentMethod\": \"kakaopay\", \"status\": \"SUCCESS\", \"items\": [{\"foodId\": null, \"foodName\": \"돈까스\", \"quantity\": 1, \"price\": 6000}]}]"
                                                    ),
                                                    @ExampleObject(
                                                            name = "구매 내역 없음",
                                                            value = "[]"
                                                    )
                                            }
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "401", description = "인증 실패")
            }
    )
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @GetMapping("/purchases")
    public ResponseEntity<?> getPurchases(@AuthenticationPrincipal String userId) {
        List<PurchaseDTO> purchases = purchaseService.getPurchasesByUserId(userId);

        if (purchases.isEmpty()) {
            // ✅ 200 OK + 빈 배열 반환
            return ResponseEntity.ok(Collections.emptyList());
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
                    description = "merchantUid : 고유 주문번호. 각 결제마다 달라야 합니다. (mid_ + 타임스탬프)로 구현하면 됩니다 // \n" +
                                  "**포트원(아임포트)**의 **IMP.request_pay()**는 JavaScript SDK입니다.\n" +
                                  "Swagger UI는 HTTP 요청만 보낼 수 있으며, JavaScript 실행 환경이 아니기 떄문에\n" +
                                  "Swagger에서는 결제창 호출이 불가능합니다.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"impUid\": \"imp_1234567890\", \n" +
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
            String impUid = purchaseDTO.getImpUid(); // ✅ 프론트에서 `imp_uid`를 직접 받아옴

            // ✅ 결제 검증 수행
            boolean isValidPayment = paymentService.verifyPayment(impUid, purchaseDTO.getTotalAmount());
            if (!isValidPayment) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 검증 실패: 자동 환불됨.");
            }

            // ✅ 결제 검증 성공 후 구매 내역 저장
            purchaseService.savePurchase(purchaseDTO, userId, impUid);
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
