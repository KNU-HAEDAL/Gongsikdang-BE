// src/main/java/com/food/controller/MenuController.java
package com.food.controller;

import com.food.config.jwt.token.JwtUtil;
import com.food.dto.MenuDTO;
import com.food.service.MenuService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Menu API", description = "메뉴 관련 API")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(
            summary = "모든 메뉴 조회",
            description = "JWT 인증을 기반으로 모든 메뉴를 조회합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "Bearer 토큰", required = true)
            }
    )
    @GetMapping("/menu")
    public ResponseEntity<List<MenuDTO>> getMenu(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거

            try {
                Claims claims = jwtUtil.extractClaims(token); // 토큰에서 클레임 추출
                String userId = claims.getSubject(); // 사용자 ID 확인

                if (userId != null) {
                    return ResponseEntity.ok(menuService.getMenuList());
                }
            } catch (Exception e) {
                System.out.println("Invalid JWT Token: " + e.getMessage());
            }
        }

        // 인증 실패 시 401 Unauthorized 반환
        return ResponseEntity.status(401).build();
    }

    @Operation(
            summary = "공식당 메뉴 조회",
            description = "공식당 A, B, C, D 메뉴를 조회합니다. 경로 변수로 타입을 전달합니다.",
            parameters = {
                    @Parameter(name = "type", description = "조회할 메뉴 타입 (a, b, c, d 중 하나)", required = true, example = "a")
            }
    )
    @GetMapping("/menu/info/{type}")
    public ResponseEntity<List<MenuDTO>> getGongsikdangMenuByType(
            @PathVariable String type) {
        System.out.println("공식당 " + type.toUpperCase() + " 매뉴 보내기 성공");

        List<MenuDTO> menuList;
        switch (type.toLowerCase()) {
            case "a":
                menuList = menuService.getGongsikdangMenu();
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

    @Operation(
            summary = "메뉴 재고 감소",
            description = "JWT 인증을 기반으로 사용자가 요청한 메뉴의 재고를 감소시킵니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "Bearer 토큰", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "감소할 메뉴 리스트와 수량을 포함한 JSON 형식의 요청 본문",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "[{\"name\": \"메뉴 이름\", \"quantity\": 2}]"
                            )
                    )
            )
    )
    @PostMapping("/menu/reduce")
    public ResponseEntity<String> reduceMenuQuantity(
            @RequestHeader("Authorization") String token,
            @RequestBody List<Map<String, Object>> cart) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            try {
                Claims claims = jwtUtil.extractClaims(token);
                String userId = claims.getSubject();

                if (userId != null) {
                    for (Map<String, Object> item : cart) {
                        String name = (String) item.get("name");
                        int quantity = (int) item.get("quantity");

                        boolean isReduced = menuService.reduceMenuQuantity(name, quantity);

                        if (!isReduced) {
                            return ResponseEntity.status(400).body("재고 부족: " + name);
                        }
                    }
                    return ResponseEntity.ok("모든 재고 감소 완료");
                }
            } catch (Exception e) {
                System.out.println("Invalid JWT Token: " + e.getMessage());
                return ResponseEntity.status(401).body("Invalid Token");
            }
        }
        return ResponseEntity.status(401).body("Unauthorized");
    }
}


