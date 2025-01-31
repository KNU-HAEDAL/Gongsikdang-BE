package com.food.controller;

import com.food.dto.PurchaseDTO;
import com.food.service.PaymentService;
import com.food.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/purchases")
    public ResponseEntity<?> getPurchases(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String userToken = token.substring(7);
        List<PurchaseDTO> purchases = purchaseService.getPurchasesByToken(userToken);

        if (purchases.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("구매 내역이 없습니다.");
        }

        return ResponseEntity.ok(purchases);
    }

    /**
     * 결제 검증 및 구매 데이터 저장 API
     */
    @Operation(summary = "구매 데이터 저장", description = "JWT 토큰과 결제 정보를 기반으로 결제 검증 후 구매 데이터를 저장합니다.")
    @PostMapping("/purchases")
    public ResponseEntity<?> savePurchase(@RequestBody PurchaseDTO purchaseDTO, @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String userToken = token.substring(7);

        try {
            purchaseService.savePurchase(purchaseDTO, userToken);
            return ResponseEntity.status(HttpStatus.CREATED).body("결제 검증 완료 및 구매 데이터 저장 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("구매 데이터 저장 중 오류 발생");
        }
    }

    /**
     * 결제 취소 API (사용자가 직접 결제 취소)
     */
    @Operation(summary = "결제 취소", description = "사용자가 결제를 취소할 수 있습니다. -> 자동결제취소가 안될경우(네트워크 문제같은) 사용자가 직접 결제를 취소할 수 있어야 합니다")
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
