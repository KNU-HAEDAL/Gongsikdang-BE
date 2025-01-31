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
    private JwtUtil jwtUtil;

    @Autowired
    private PaymentService paymentService; // ✅ 변경: PaymentService를 사용


    /**
     * JWT 토큰 기반으로 사용자의 구매 내역 조회
     */
    public List<PurchaseDTO> getPurchasesByToken(String userToken) {
        String userId = jwtUtil.extractUserId(userToken); // JWT에서 userId 추출
        return purchaseMapper.findPurchasesByUserId(userId);
    }

    /**
     * 결제 검증 후 구매 내역 저장 (트랜잭션 적용)
     */
    @Transactional
    public void savePurchase(PurchaseDTO purchaseDTO, String userToken) {
        String userId = jwtUtil.extractUserId(userToken);
        purchaseDTO.setUserId(userId);

        boolean isValidPayment = paymentService.verifyPayment(purchaseDTO.getMerchantUid(), purchaseDTO.getTotalAmount());
        if (!isValidPayment) {
            paymentService.cancelPayment(purchaseDTO.getMerchantUid(), "결제 검증 실패로 인한 자동 환불");
            throw new RuntimeException("결제 검증 실패: 구매 데이터를 저장할 수 없습니다.");
        }

        purchaseMapper.insertPurchase(purchaseDTO);

        for (ItemDTO item : purchaseDTO.getItems()) {
            item.setFoodId(purchaseDTO.getFoodId());
            purchaseMapper.insertItem(item);
        }
    }
}
