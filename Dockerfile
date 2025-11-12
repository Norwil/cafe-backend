FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY application/pom.xml ./application/
COPY menu/pom.xml ./menu/
COPY orders/pom.xml ./orders/
COPY event/pom.xml ./event/
COPY users/pom.xml ./users/

RUN mvn dependency:go-offline

COPY . .

RUN mvn clean package -DskipTests

# -----------------------------------------------------

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /app/application/target/application-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]