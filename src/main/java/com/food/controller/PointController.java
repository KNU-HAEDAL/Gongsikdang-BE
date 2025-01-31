package com.food.controller;

import com.food.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/point")
@Tag(name = "Point API", description = "포인트 관련 API")
public class PointController {

    @Operation(summary = "포인트 조회", description = "사용자의 포인트를 조회합니다.")
    @GetMapping
    public ResponseEntity<Integer> getPoint() {
        // TODO: 포인트 조회 로직
        return ResponseEntity.ok(0); // 예시: 포인트 0 리턴
    }

    @Operation(summary = "포인트 충전", description = "사용자의 포인트를 충전합니다.")
    @PostMapping
    public ResponseEntity<String> chargePoint(@RequestBody UserDTO userDTO) {
        // TODO: 포인트 충전 로직
        return ResponseEntity.ok("포인트 충전 성공");
    }
}
