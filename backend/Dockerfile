FROM eclipse-temurin:25-jdk AS compiledjar

WORKDIR /app

COPY build.gradle /app

COPY gradlew /app

COPY gradle/wrapper /app/gradle/wrapper

COPY settings.gradle /app

COPY src /app/src

RUN chmod +x gradlew

RUN ./gradlew bootJar



FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=compiledjar /app/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]