FROM openjdk:17-jdk-slim

WORKDIR /app

# Gradle wrapper와 build.gradle 파일들을 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 의존성 다운로드를 위해 gradle 실행
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드
RUN ./gradlew build -x test --no-daemon

# 실행 가능한 JAR 파일을 app.jar로 복사
RUN cp build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8081

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"] 