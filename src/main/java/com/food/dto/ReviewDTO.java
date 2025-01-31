package com.food.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;

@Getter
@Setter
@ToString
public class ReviewDTO {
    private int reviewId;
    private String userId;
    private int foodId;
    private String reviewContent;
    private String grade;
    private Date reviewDate;
}
