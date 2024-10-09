FROM  eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .

RUN dos2unix gradlew

RUN chmod +x gradlew

RUN ./gradlew build -x test

FROM eclipse-temurin:17-jre-alpine AS production
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
COPY /wait-for-it.sh /wait-for-it.sh

# Install bash
RUN apk add --no-cache bash
RUN chmod +x /wait-for-it.sh

ENTRYPOINT ["/wait-for-it.sh", "config-server.internal:8000", "--", "java","-jar","app.jar"]