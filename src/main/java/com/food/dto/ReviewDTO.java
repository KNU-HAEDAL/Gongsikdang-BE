package com.food.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;

@Getter
@Setter
@ToString
public class ReviewDTO {
    private Integer reviewId;
    private String userId;
    private Integer foodId;
    private String reviewContent;
    private Integer grade;
    private Date reviewDate;
}
