FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY . .
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app
RUN useradd --system --create-home --uid 10001 appuser
COPY --from=build /workspace/equipment-server/target/equipment-server-1.0-SNAPSHOT.jar /app/app.jar
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
