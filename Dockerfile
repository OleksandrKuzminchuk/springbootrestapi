FROM maven:3.8.4-openjdk-17 AS builder

WORKDIR /app

COPY ./pom.xml ./pom.xml

RUN mvn dependency:go-offline -B

COPY . ./

RUN mvn package -DskipTests

FROM openjdk:17-jdk-alpine

WORKDIR /app

ENV SPRING_PROFILES_ACTIVE docker

COPY --from=builder /app/target/springbootrestapi-0.0.1-SNAPSHOT.jar /app/springbootrestapi.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/springbootrestapi.jar"]
