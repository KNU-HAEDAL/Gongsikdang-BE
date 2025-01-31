package com.food.service;

import com.food.dto.PurchaseDTO;
import com.food.dto.ItemDTO;
import com.food.mapper.PurchaseMapper;
import com.food.config.jwt.token.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseMapper purchaseMapper;
    private final JwtUtil jwtUtil;

    public PurchaseServiceImpl(PurchaseMapper purchaseMapper, JwtUtil jwtUtil) {
        this.purchaseMapper = purchaseMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public List<PurchaseDTO> getPurchasesByToken(String userToken) {
        String userId = jwtUtil.extractUsername(userToken);
        return purchaseMapper.findPurchasesByUserId(userId);
    }

    @Override
    public void savePurchase(PurchaseDTO purchaseDTO, String userToken) {
        String userId = jwtUtil.extractUsername(userToken);
        purchaseDTO.setUserId(userId);

        // 구매 정보 저장
        purchaseMapper.insertPurchase(purchaseDTO);

        // 구매 상품 저장
        for (ItemDTO item : purchaseDTO.getItems()) {
            item.setFoodId(purchaseDTO.getFoodId()); // 수정: purchaseId 대신 foodId 사용
            purchaseMapper.insertItem(item);
        }
    }
}

