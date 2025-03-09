package com.food.controller;

import com.food.config.jwt.token.JwtUtil;
import com.food.dto.ReviewDTO;
import com.food.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    @Operation(
            summary = "리뷰 작성",
            description = "특정 음식에 대한 리뷰를 작성합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "리뷰 작성 요청 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{ \"foodId\": 1, \"reviewContent\": \"정말 맛있어요!\", \"grade\": 5 }"
                            )
                    )
            )
    )
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @PostMapping("/api/review/write")
    public ResponseEntity<String> addReview(
            @AuthenticationPrincipal String userId,
            @RequestBody Map<String, Object> requestBody
    ) {
        // Request Body에서 값 추출
        int foodId = (int) requestBody.get("foodId");
        String reviewContent = (String) requestBody.get("reviewContent");
        int grade = (int) requestBody.get("grade");

        // ReviewDTO에 값 설정
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setUserId(userId);
        reviewDTO.setFoodId(foodId);
        reviewDTO.setReviewContent(reviewContent);
        reviewDTO.setGrade(grade);

        // 리뷰 저장 로직 호출
        reviewService.insertReview(reviewDTO);

        // 성공 응답 반환
        return ResponseEntity.ok("리뷰 작성 성공");
    }

    @Operation(
            summary = "리뷰 조회",
            description = "특정 음식의 리뷰를 위한 이름을 가져옵니다. \n"
    )
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 인증 필요
    @GetMapping("/read/{foodId}")
    public ResponseEntity<String> getReviewName(
            @RequestBody Map<String, Object> requestBody
    ) {
        int foodId = (int) requestBody.get("foodId");
        String foodName = reviewService.getReviewName(foodId);
        return ResponseEntity.ok(foodName);
    }
}
