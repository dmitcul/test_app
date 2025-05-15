FROM amazoncorretto:17-alpine AS build
WORKDIR /app

COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean build -x test


FROM amazoncorretto:17-alpine
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar
COPY --from=build /app/${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]