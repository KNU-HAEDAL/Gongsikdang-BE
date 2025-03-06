package com.food.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemDTO {
    private int itemId; //상품 필드 고유 ID(auto)
    private Integer foodId; // 음식의 고유 ID
    private String foodName; // 상품명
    private Integer grade; // 평점
    private Integer quantity; // 구매 수량
    private Integer price; // 상품 가격
    private String purchaseId; // 구매아이디
}


