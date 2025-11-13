# ===== Build Stage =====
FROM eclipse-temurin:8-jdk-alpine AS build

ARG WORK_DIR
WORKDIR /app

# Allow Maven + Java to negotiate TLS 1.2 and TLS 1.3
ENV MAVEN_OPTS="-Dhttps.protocols=TLSv1.3,TLSv1.2 -Djdk.tls.client.protocols=TLSv1.3,TLSv1.2"

# Install Maven and build
RUN apk add --no-cache maven

# Copy Maven project files
COPY ${WORK_DIR}/pom.xml ./pom.xml
COPY build/maven/start.sh ./start.sh
COPY ${WORK_DIR}/src ./src

# Build JAR package
RUN mvn -B -f /app/pom.xml clean package

# ===== Runtime Stage =====
FROM eclipse-temurin:8-jre-alpine

WORKDIR /opt/egov

COPY --from=build /app/target/*.jar /app/start.sh /opt/egov/
RUN chmod +x /opt/egov/start.sh

CMD ["/opt/egov/start.sh"]
