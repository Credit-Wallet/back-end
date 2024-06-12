FROM  eclipse-temurin:17-jdk-alpine as build
WORKDIR /app
COPY . .

RUN dos2unix gradlew

RUN ./gradlew build -x test

FROM eclipse-temurin:17-jre-alpine as production
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]