package com.food.mapper;

import com.food.dto.ReviewDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {

    List<ReviewDTO> selectAllReviews(Integer foodId, String sort);

    void insertReview(@Param("review") ReviewDTO reviewDTO);

    void updateFoodAvgStarRating(Integer foodId);
}
