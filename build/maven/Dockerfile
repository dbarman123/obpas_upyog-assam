# ===== Build Stage =====
FROM eclipse-temurin:8u372-jdk-alpine AS build
# Or use: adoptopenjdk/openjdk8:ubi-minimal-jdk8u372-b07

ARG WORK_DIR
WORKDIR /app

# Copy Maven project files
COPY ${WORK_DIR}/pom.xml ./pom.xml
COPY build/maven/start.sh ./start.sh
COPY ${WORK_DIR}/src ./src

# Build JAR
RUN apk add --no-cache maven && mvn -B -f /app/pom.xml clean package

# ===== Runtime Stage =====
FROM eclipse-temurin:8u372-jre-alpine
# Or: adoptopenjdk/openjdk8:ubi-minimal-jre8u372-b07

WORKDIR /opt/egov

COPY --from=build /app/target/*.jar /app/start.sh /opt/egov/
RUN chmod +x /opt/egov/start.sh

CMD ["/opt/egov/start.sh"]
