package com.food.controller;

import com.food.dto.MenuDTO;
import com.food.service.MenuService;
import com.food.config.jwt.token.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Menu API", description = "메뉴 관련 API")
@RequestMapping("api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "모든 메뉴 조회", description = "모든 메뉴를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<MenuDTO>> getMenu() {
        List<MenuDTO> menuList = menuService.getMenuList();
        return ResponseEntity.ok(menuList);
    }

    @Operation(summary = "공식당 메뉴 조회", description = "공식당 A, B, C, D 메뉴를 조회합니다.")
    @GetMapping("/info/{type}")
    public ResponseEntity<List<MenuDTO>> getGongsikdangMenuByType(@PathVariable String type) {
        List<MenuDTO> menuList;
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

    @Operation(summary = "메뉴 재고 감소", description = "JWT 토큰을 검증하고 사용자가 요청한 메뉴의 재고를 감소시킵니다.")
    @PostMapping("/reduce")
    public ResponseEntity<String> reduceMenuQuantity(
            @RequestHeader("Authorization") String token,
            @RequestBody List<Map<String, Object>> cart) {

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            // 토큰 검증 및 사용자 ID 추출
            String userId = jwtUtil.extractUserId(token); // JWT에서 사용자 ID 추출

            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.status(401).body("Can Not Find Token");
            }

            // 재고 감소 처리
            for (Map<String, Object> item : cart) {
                String name = (String) item.get("name");
                int quantity = (int) item.get("quantity");

                boolean isReduced = menuService.reduceMenuQuantity(name, quantity);

                if (!isReduced) {
                    return ResponseEntity.status(400).body("재고 부족: " + name);
                }
            }

            return ResponseEntity.ok("모든 재고 감소 완료");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid Token");
        }
    }
}

