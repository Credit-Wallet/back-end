# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .

RUN dos2unix gradlew

RUN chmod +x gradlew

RUN ./gradlew build -x test

# Stage 2: Production
FROM eclipse-temurin:17-jre-alpine AS production
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
