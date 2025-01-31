package com.food.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MenuDTO {
    private Integer foodId;
    private String foodName;
    private String price;
    private String number;
    private String sector;
    private double avgStarRating;
}
