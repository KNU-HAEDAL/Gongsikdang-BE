package com.food.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MenuDTO {
    private String name;
    private String sector;
    private String price;
    private String number;
    private String restaurantName;
    private String image;
}
