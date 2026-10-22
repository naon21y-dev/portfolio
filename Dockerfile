FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY backend/ ./backend/
COPY frontend/src/data/ ./frontend/src/data/
WORKDIR /app/backend
RUN chmod +x gradlew && ./gradlew --no-daemon bootJar
RUN cp build/libs/*-SNAPSHOT.jar /app/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/app.jar ./app.jar
ENV SERVER_ADDRESS=0.0.0.0
EXPOSE 10000
CMD ["sh", "-c", "exec java -XX:MaxRAMPercentage=70 -jar app.jar --server.port=${PORT:-10000}"]
