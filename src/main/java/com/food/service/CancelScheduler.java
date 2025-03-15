package com.food.service;

import com.food.dto.PurchaseDTO;
import com.food.mapper.PurchaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CancelScheduler {

    @Autowired
    private PurchaseMapper purchaseMapper;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PointService pointService;

    // ✅ 30분마다 실행 (1800000ms)
    @Scheduled(fixedRate = 1800000) // 10분마다 실행
    public void autoCancelPayments() {
        log.info("🔄 [자동 결제 취소] 시작");

        List<PurchaseDTO> expiredPayments = purchaseMapper.findExpiredPayments();
        if (expiredPayments.isEmpty()) {
            log.info("✅ [자동 결제 취소] 취소할 결제 없음.");
            return;
        }

        for (PurchaseDTO purchase : expiredPayments) {
            String impUid = purchase.getImpUid();
            String userId = purchase.getUserId();
            Integer refundAmount = purchase.getTotalAmount();

            log.info("🚨 [자동 결제 취소] impUid: {} | userId: {} | 금액: {}", impUid, userId, refundAmount);

            // ✅ 결제 취소 API 호출
            boolean isCancelled = paymentService.cancelPayment(impUid, "1시간 미사용 자동 취소");
            if (!isCancelled) {
                log.error("❌ [자동 결제 취소 실패] impUid: {}", impUid);
                continue;
            }

            // ✅ 포인트 적립
            try {
                pointService.addPoints(userId, refundAmount);
                log.info("✅ [포인트 적립 완료] userId: {} | 금액: {}", userId, refundAmount);
            } catch (Exception e) {
                log.error("❌ [포인트 적립 실패] userId: {} | 오류: {}", userId, e.getMessage());
            }

            // ✅ DB에서 삭제
            try {
                purchaseService.deletePurchaseByImpUid(impUid);
                log.info("✅ [DB 삭제 완료] impUid: {}", impUid);
            } catch (Exception e) {
                log.error("❌ [DB 삭제 실패] impUid: {} | 오류: {}", impUid, e.getMessage());
            }
        }
        log.info("✅ [자동 결제 취소] 완료");
    }
}
