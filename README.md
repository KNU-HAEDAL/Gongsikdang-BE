# Gongsikdang-BE

📌 프로젝트 개요

공식당 예약 사이트의 백엔드 시스템은 Spring Boot 기반으로 개발되었으며, 식당 예약, 메뉴 조회, 결제 관리 등의 기능을 제공합니다. JWT 기반 인증을 적용하였으며, 포트원(아임포트)을 활용한 결제 검증을 수행합니다.

📌 주요 기능

🔹 사용자 인증 및 관리

JWT 기반 로그인 및 회원가입

관리자 인증을 통한 시스템 관리

🔹 식당 예약 기능

사용자가 예약 가능 시간 확인 및 예약 등록

예약 조회 및 취소 기능

🔹 메뉴 관리

식당 메뉴 조회 (GET /api/menu)

메뉴 재고 감소 기능 (POST /api/menu/reduce)

🔹 결제 및 결제 검증

포트원(아임포트) 결제 연동

백엔드에서 결제 검증 후 구매 데이터 저장 (POST /api/purchases)

결제 취소 기능 제공 (POST /api/purchases/cancel)
