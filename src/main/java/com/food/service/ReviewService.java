package com.food.service;

import com.food.dto.ReviewDTO;
import com.food.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    public List<ReviewDTO> getAllReviews(Integer foodId, String sort) {
        return reviewMapper.selectAllReviews(foodId, sort);
    }

    public void insertReview(ReviewDTO reviewDTO) {
        reviewMapper.insertReview(reviewDTO);
    }

}
