package com.food.controller;

import com.food.dto.ReviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@Tag(name = "Review API", description = "리뷰 관련 API")
public class ReviewController {

    @Operation(summary = "리뷰 조회", description = "특정 음식의 리뷰를 조회합니다.")
    @GetMapping("/read/{foodId}")
    public ResponseEntity<List<ReviewDTO>> getReviews(@PathVariable Long foodId) {
        // TODO: 리뷰 조회 로직
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "리뷰 작성", description = "특정 음식에 대한 리뷰를 작성합니다.")
    @PostMapping("/write/{foodId}")
    public ResponseEntity<String> addReview(@PathVariable Long foodId, @RequestBody ReviewDTO reviewDTO) {
        // TODO: 리뷰 작성 로직
        return ResponseEntity.ok("리뷰 작성 성공");
    }
}
