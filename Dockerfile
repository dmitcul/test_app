FROM amazoncorretto:17-alpine AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
RUN ./gradlew --version

COPY . .
RUN ./gradlew clean build -x test


FROM amazoncorretto:17-alpine
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar
COPY --from=build /app/${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
