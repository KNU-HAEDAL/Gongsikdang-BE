package com.food.service;

import com.food.dto.ReviewDTO;
import com.food.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    public List<ReviewDTO> getAllReviews(Integer foodId, String sort) {
        return reviewMapper.selectAllReviews(foodId, sort);
    }

    @Transactional // 트랜잭션 적용
    public void insertReview(ReviewDTO reviewDTO) {
        reviewMapper.insertReview(reviewDTO);
        reviewMapper.updateFoodAvgStarRating(reviewDTO.getFoodId()); // 평균 별점 업데이트
    }

    public String getReviewName(Integer foodId) {
        return reviewMapper.selectReviewName(foodId);
    }

}
