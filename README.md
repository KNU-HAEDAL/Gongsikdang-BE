# 🍱 Gongsikdang-BE

공식당(Gongsikdang) 백엔드 레포지토리입니다.  
React 기반 프론트와 협업하여 예약/결제 시스템을 구현하였습니다.

---

## 🛠 주요 기술
- Java, Spring Boot, MySQL, JWT, Maven, Swagger
- Dockerfile 기반 컨테이너화 → Railway 배포

![Tech Stack](https://github.com/user-attachments/assets/2d125323-6c5d-4464-8224-ed8b7fb95ff8)

---

## 📐 ERD (데이터베이스 설계)

메뉴, 예약, 리뷰, 포인트 등 주요 기능을 중심으로 설계된 데이터베이스 구조입니다:

![ERD](https://github.com/user-attachments/assets/bc537204-1008-4b96-9bb8-e6767a51735d)

---

## 주요 기능
1. **메뉴 확인**
   - 카테고리별 메뉴 출력 API
   - 결제 시 수량 차감 및 재고 반영

2. **예약 제한**
   - **2시간 전부터만 예약 가능** (예약 시각 제한 로직)

3. **JWT 로그인 인증**
   - 로그인/회원가입 및 JWT 발급
   - 모든 사용자 API에 토큰 기반 인증 적용

4. **테스트 결제 기능 (QR 기반)**
   - 포트원(아임포트) API 연동으로 QR 결제 구현
   - **결제 완료 후 24시간 경과 시 예약 자동 삭제**

5. **포인트 충전 기능**
   - 마이페이지에서 사용자 포인트 충전
   - 결제 시 포인트 사용 가능

6. **리뷰 시스템**
   - 결제 완료 사용자 대상 리뷰 등록/조회 가능
   - 관리자 없이 자유 CRUD 가능

---

## 🤝 협업 구조
- **프론트엔드**: [Gongsikdang-FE](https://github.com/KNU-HAEDAL/Gongsikdang-FE) (React 기반)
- **백엔드**: 본 레포지토리에서 Spring Boot 기반 API 및 비즈니스 로직 구현

---

## 📚 Swagger 문서
> 현재 서버 중지 상태이며, 향후 Swagger 이미지 또는 링크 추가 예정
