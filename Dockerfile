# Java 25 (Temurin) — matches the local toolchain (~/.jdks/temurin-25.0.2).
# `--enable-preview` is required because the pom compiles with that flag;
# class files of preview features won't load on a non-preview JVM.
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
    "--enable-preview", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
