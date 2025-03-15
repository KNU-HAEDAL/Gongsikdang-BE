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

    // 사용자 impUid로 특정 구매 내역 조회
    PurchaseDTO findByImpUid(@Param("impUid") String impUid);

    // 사용자 purchaseId로 특정 구매 내역 삭제
    void deleteItemsByPurchaseId(@Param("purchaseId") Integer purchaseId);

    // 사용자 purchaseId로 특정 구매 상품 삭제
    void deletePurchaseById(@Param("purchaseId") Integer purchaseId);

    // 구매 정보 저장
    void insertPurchase(@Param("purchase") PurchaseDTO purchaseDTO);

    // 아이템 정보 저장
    void insertItem(@Param("item") ItemDTO itemDTO);
}

