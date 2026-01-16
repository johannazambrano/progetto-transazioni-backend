FROM maven:3.9.6-eclipse-temurin-17-alpine
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY . .
EXPOSE 9091
CMD ["mvn", "quarkus:dev", "-Dquarkus.http.host=0.0.0.0"]
