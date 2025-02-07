# 1️⃣ Java 19 기반의 경량 OpenJDK 이미지 사용
FROM openjdk:19-jdk

# 2️⃣ 작업 디렉토리 설정
WORKDIR /app

# 3️⃣ 로컬 파일을 컨테이너 내부로 복사
COPY . /app/.

# 4️⃣ Maven Wrapper 실행 권한 부여 및 빌드 수행
RUN chmod +x mvnw \
    && ./mvnw clean package -DskipTests

# 5️⃣ 애플리케이션 실행할 포트 설정 (Spring Boot 기본 8080)
EXPOSE 8080

# 6️⃣ 컨테이너 시작 시 실행할 명령어 (JAR 실행)
CMD ["java", "-jar", "target/*.jar"]
