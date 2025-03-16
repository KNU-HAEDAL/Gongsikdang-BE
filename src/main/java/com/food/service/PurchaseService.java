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

    @Autowired
    private PointService pointService;


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

        try {
            // ✅ 포인트 사용한 경우 차감
            if (purchaseDTO.getUsedPoints() != null && purchaseDTO.getUsedPoints() > 0) {
                pointService.usePoint(userId, purchaseDTO.getUsedPoints()); // ✅ 포인트 차감
                System.out.println("✅ 포인트 차감 완료: " + purchaseDTO.getUsedPoints());
            }


            purchaseMapper.insertPurchase(purchaseDTO);
            System.out.println("✅ 구매 내역 저장 완료: " + purchaseDTO);

            for (ItemDTO item : purchaseDTO.getItems()) {
                item.setPurchaseId(purchaseDTO.getPurchaseId());
                purchaseMapper.insertItem(item);
            }
        } catch (Exception e) {
            System.out.println("❌ 데이터 저장 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("구매 내역 저장 실패", e);
        }
    }

    public void deletePurchaseByImpUid(String impUid) {
        PurchaseDTO purchase = purchaseMapper.findByImpUid(impUid);
        int purchaseId = purchase.getPurchaseId();

        if (purchase != null) {
            // ✅ 먼저 item 테이블의 데이터를 삭제
            purchaseMapper.deleteItemsByPurchaseId(purchaseId);

            // ✅ 그 다음 purchaseList 테이블의 데이터를 삭제
            purchaseMapper.deletePurchaseById(purchaseId);
        } else {
            throw new RuntimeException("결제 내역을 찾을 수 없습니다.");
        }
    }

}
