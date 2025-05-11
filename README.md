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

## 🔍 핵심 기능
- 메뉴 확인 / 예약 생성 (2시간 전부터만 가능)
- 포트원 결제 (QR 방식) / **24시간 후 예약 자동 삭제**
- JWT 로그인 기반 사용자 인증
- 포인트 충전 (마이페이지) / 리뷰 작성 기능

---

## 🤝 협업 구조
- **프론트엔드**: [Gongsikdang-FE](https://github.com/KNU-HAEDAL/Gongsikdang-FE) (React 기반)
- **백엔드**: 본 레포지토리에서 Spring Boot 기반 API 및 비즈니스 로직 구현

---

## 📚 Swagger 문서
> 현재 서버 중지 상태이며, 향후 Swagger 이미지 또는 링크 추가 예정
