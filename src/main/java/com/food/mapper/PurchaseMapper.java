package com.food.mapper;

import com.food.dto.PurchaseDTO;
import com.food.dto.ItemDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseMapper {

    // 사용자 ID로 구매 내역 조회
    List<PurchaseDTO> findPurchasesByUserId(@Param("userId") String userId);

    // 구매 정보 저장
    void insertPurchase(@Param("purchase") PurchaseDTO purchaseDTO);

    // 아이템 정보 저장
    void insertItem(@Param("item") ItemDTO itemDTO);
}

