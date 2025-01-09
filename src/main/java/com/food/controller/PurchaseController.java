package com.food.controller;

import com.food.dto.PurchaseDTO;
import com.food.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Purchase API", description = "구매 데이터 관련 API")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @Operation(
            summary = "구매 내역 조회",
            description = "사용자의 JWT 토큰을 기반으로 구매 내역을 조회합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "Bearer 토큰", required = true, example = "Bearer eyJhbGci...")
            }
    )
    @GetMapping("/purchases")
    public ResponseEntity<?> getPurchases(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String userToken = token.substring(7); // "Bearer " 이후의 토큰 값 추출
        List<PurchaseDTO> purchases = purchaseService.getPurchasesByToken(userToken);

        if (purchases.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("구매 내역이 없습니다.");
        }

        return ResponseEntity.ok(purchases);
    }

    @Operation(
            summary = "구매 데이터 저장",
            description = "JWT 토큰과 구매 데이터를 기반으로 새로운 구매 데이터를 저장합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "Bearer 토큰", required = true, example = "Bearer eyJhbGci...")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "저장할 구매 데이터를 JSON 형식으로 전달합니다.",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = """
                    {
                        "merchantUid": "unique-id-123",
                        "userId": "testUser",
                        "date": "2025-01-06T12:00:00",
                        "totalAmount": 10000,
                        "paymentMethod": "credit_card",
                        "status": "paid",
                        "items": [
                            {
                                "name": "item1",
                                "quantity": 2,
                                "price": 5000
                            }
                        ]
                    }
                    """
                            )
                    )
            )
    )
    @PostMapping("/purchases")
    public ResponseEntity<?> savePurchase(
            @RequestBody PurchaseDTO purchaseDTO,
            @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String userToken = token.substring(7); // "Bearer " 이후의 토큰 값 추출

        try {
            purchaseService.savePurchase(purchaseDTO, userToken);
            return ResponseEntity.status(HttpStatus.CREATED).body("구매 데이터 저장 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("구매 데이터 저장 중 오류 발생");
        }
    }
}

