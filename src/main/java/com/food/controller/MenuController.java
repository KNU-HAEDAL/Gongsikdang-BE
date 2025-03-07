package com.food.controller;

import com.food.dto.MenuDTO;
import com.food.service.MenuService;
import com.food.config.jwt.token.JwtUtil;
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

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Menu API", description = "메뉴 관련 API")
@RequestMapping("api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Operation(summary = "모든 메뉴 조회", description = "모든 메뉴를 조회합니다.")
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @GetMapping
    public ResponseEntity<List<MenuDTO>> getMenu() {
        System.out.println("🔥 매뉴조회시작");
        List<MenuDTO> menuList = menuService.getMenuList();
        return ResponseEntity.ok(menuList);
    }

    @Operation(summary = "공식당 메뉴 조회", description = "공식당 A, B, C, D 메뉴를 조회합니다.")
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @GetMapping("/info/{type}")
    public ResponseEntity<List<MenuDTO>> getGongsikdangMenuByType(@PathVariable String type) {
        List<MenuDTO> menuList;

        System.out.println("🔥 구역별 매뉴조회시작");

        switch (type.toLowerCase()) {
            case "a":
                menuList = menuService.getGongsikdang_AMenu();
                break;
            case "b":
                menuList = menuService.getGongsikdang_BMenu();
                break;
            case "c":
                menuList = menuService.getGongsikdang_CMenu();
                break;
            case "d":
                menuList = menuService.getGongsikdang_DMenu();
                break;
            default:
                return ResponseEntity.badRequest().build(); // 잘못된 타입 처리
        }

        return ResponseEntity.ok(menuList);
    }

    /**
     * 메뉴 재고 감소 API
     */
    @Operation(
            summary = "메뉴 재고 감소",
            description = "JWT 토큰을 검증하고 사용자가 요청한 메뉴의 재고를 감소시킵니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "장바구니에 담긴 메뉴 목록과 수량 정보",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "[ " +
                                            "{ \"foodId\": 1, \"foodName\": \"돈까스\", \"quantity\": 2 }," +
                                            " { \"foodId\": 2, \"foodName\": \"떡볶이\", \"quantity\": 1 } " +
                                            "]"
                            )
                    )
            )
    )
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @PostMapping("/reduce")
    public ResponseEntity<String> reduceMenuQuantity(
            @AuthenticationPrincipal String userId,
            @RequestBody List<Map<String, Object>> cart) {
        try {
            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.status(401).body("Can Not Find Token");
            }

            // 재고 감소 처리
            for (Map<String, Object> item : cart) {
                int foodId = (int) item.get("foodId");
                int quantity = (int) item.get("quantity");

                boolean isReduced = menuService.reduceMenuQuantity(foodId, quantity);

                if (!isReduced) {
                    return ResponseEntity.status(400).body("재고 부족: " + foodId);
                }
            }

            return ResponseEntity.ok("모든 재고 감소 완료");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid Token");
        }
    }
}

