package com.food.controller;

import com.food.dto.ReviewDTO;
import com.food.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/review")
@Tag(name = "Review API", description = "리뷰 관련 API")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Operation(
            summary = "리뷰 조회",
            description = "특정 음식의 리뷰 목록을 가져옵니다. \n\n" +
                    "- `sort` 값에 따라 정렬 방식이 변경됩니다.\n" +
                    "- `sort=desc` (기본값) → 별점 높은 순\n" +
                    "- `sort=asc` → 별점 낮은 순"
    )
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @GetMapping("/read/{foodId}")
    public ResponseEntity<List<ReviewDTO>> getReviews(
            @PathVariable Integer foodId,
            @Parameter(name = "sort", description = "정렬 방식 (asc: 별점 낮은 순, desc: 별점 높은 순)", example = "desc")
            @RequestParam(defaultValue = "desc") String sort
    ) {
        System.out.println("🔥 리뷰조회시작");
        List<ReviewDTO> reviewDTOList = reviewService.getAllReviews(foodId, sort);
        return ResponseEntity.ok(reviewDTOList);
    }


    @Operation(summary = "리뷰 작성", description = "특정 음식에 대한 리뷰를 작성합니다.")
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @PostMapping("/write")
    public ResponseEntity<String> addReview( @RequestBody ReviewDTO reviewDTO) {
        System.out.println("🔥 리뷰작성시작");
        reviewService.insertReview(reviewDTO);
        return ResponseEntity.ok("리뷰 작성 성공");
    }
}
