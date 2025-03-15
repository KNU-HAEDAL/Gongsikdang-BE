package com.food.service;

import com.food.dto.PurchaseDTO;
import com.food.dto.ItemDTO;
import com.food.mapper.PurchaseMapper;
import com.food.config.jwt.token.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PurchaseService {
    @Autowired
    private PurchaseMapper purchaseMapper;

    @Autowired
    private PaymentService paymentService; // ✅ 변경: PaymentService를 사용


    /**
     * JWT 토큰 기반으로 사용자의 구매 내역 조회
     */
    public List<PurchaseDTO> getPurchasesByUserId(String userId) {
        return purchaseMapper.findPurchasesByUserId(userId);
    }

    /**
     * 결제 검증 후 구매 내역 저장 (트랜잭션 적용)
     */
    @Transactional
    public void savePurchase(PurchaseDTO purchaseDTO, String userId) {
        purchaseDTO.setUserId(userId);

        // ✅ 결제 검증 성공 시 구매 내역 저장
        purchaseMapper.insertPurchase(purchaseDTO);

        for (ItemDTO item : purchaseDTO.getItems()) {
            item.setPurchaseId(purchaseDTO.getPurchaseId());
            purchaseMapper.insertItem(item);
        }
    }

}
