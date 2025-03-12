package com.food.service;

import com.food.dto.UserDTO;
import com.food.mapper.PointMapper;
import com.food.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointService {

    @Autowired
    private PointMapper pointMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PaymentService paymentService; // ✅ 공통 결제 검증 서비스 사용

    /**
     * 🔥 포인트 조회
     */
    public int getUserPoint(String userId) {
        UserDTO user = userMapper.findByUsername(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return pointMapper.getUserPoint(userId);
    }

    /**
     * 🔥 포인트 충전 (트랜잭션 적용) + 충전 실패 시 자동 환불
     */
    @Transactional
    public void savePoint(String userId, int point, String impUid) {
        UserDTO user = userMapper.findByUsername(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // ✅ 포트원 결제 검증 수행
        boolean isValidPayment = paymentService.verifyPayment(impUid, point);
        if (!isValidPayment) {
            throw new RuntimeException("결제 검증 실패: 포인트 충전을 취소합니다.");
        }

        // ✅ 포인트 충전 시도
        try {
            pointMapper.updatePoint(userId, point); // 포인트 충전
        } catch (Exception e) {
            // 💡 포인트 충전 실패 시 자동 환불
            paymentService.cancelPayment(impUid, "포인트 충전 실패로 인한 자동 환불");
            throw new RuntimeException("포인트 충전 중 오류 발생. 결제를 취소합니다.");
        }
    }
}
