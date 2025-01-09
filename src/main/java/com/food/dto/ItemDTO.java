package com.food.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemDTO {
    private Integer id; // Auto Increment된 고유 ID
    private Integer purchaseId; // 연관된 Purchase ID
    private String name; // 상품명
    private Integer quantity; // 구매 수량
    private Integer price; // 상품 가격
}


