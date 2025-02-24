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
    public void savePurchase(PurchaseDTO purchaseDTO, String userId, String impUid) {
        purchaseDTO.setUserId(userId);

        // ✅ 포트원 결제 검증 수행 (`imp_uid`로 검증)
        boolean isValidPayment = paymentService.verifyPayment(impUid, purchaseDTO.getTotalAmount());
        if (!isValidPayment) {
            paymentService.cancelPayment(impUid, "결제 검증 실패로 인한 자동 환불");
            throw new RuntimeException("결제 검증 실패: 구매 데이터를 저장할 수 없습니다.");
        }

        // ✅ 결제 검증 성공 시 구매 내역 저장
        purchaseMapper.insertPurchase(purchaseDTO);

        for (ItemDTO item : purchaseDTO.getItems()) {
            item.setFoodId(purchaseDTO.getFoodId());
            purchaseMapper.insertItem(item);
        }
    }

}
