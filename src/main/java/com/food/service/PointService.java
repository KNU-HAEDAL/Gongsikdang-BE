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

    public void addPoints(String userId, int amount) {
        pointMapper.updatePoint(userId, amount); // 포인트 충전
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
            pointMapper.updatePoint(userId, point * 110); // 포인트 충전
        } catch (Exception e) {
            // 💡 포인트 충전 실패 시 자동 환불
            paymentService.cancelPayment(impUid, "포인트 충전 실패로 인한 자동 환불");
            throw new RuntimeException("포인트 충전 중 오류 발생. 결제를 취소합니다.");
        }
    }

    public void usePoint(String userId, int point){
        UserDTO user = userMapper.findByUsername(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // ✅ 사용자의 현재 포인트 조회
        int currentPoint = user.getPoint();
        System.out.println("현재포인트 : " + currentPoint);
        System.out.println("차감된포인트 : " + point);
        if (currentPoint < point) {
            System.out.println("✅ 포인트 부족.");
            throw new RuntimeException("포인트 부족: 사용 가능한 포인트보다 큰 금액을 사용할 수 없습니다.");
        }

        // ✅ 포인트 차감
        try {
            pointMapper.deductPoint(userId, point);
            System.out.println("✅ 포인트 사용 완료. 남은 포인트: " + (currentPoint - point));
        } catch (Exception e) {
            System.out.println("✅ 포인트 사용 실패.");
            throw new RuntimeException("포인트 차감 중 오류 발생.");
        }
    }
}
