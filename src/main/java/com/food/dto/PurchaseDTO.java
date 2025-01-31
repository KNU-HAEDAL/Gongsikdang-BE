package com.food.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class PurchaseDTO {
    private Integer foodId; // Auto Increment된 고유 ID
    private String merchantUid; // 주문 고유 번호
    private String userId; // 사용자 ID
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date date; // 구매 날짜
    private Integer totalAmount; // 총 결제 금액
    private String paymentMethod; // 결제 방식
    private String status; // 주문 상태
    private List<ItemDTO> items; // 구매한 상품 리스트
}




