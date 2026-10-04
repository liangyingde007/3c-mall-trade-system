FROM maven:3.9.9-eclipse-temurin-8 AS build
WORKDIR /build
COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN mvn -B -ntp package -DskipTests

FROM eclipse-temurin:8-jre-jammy
# Don't start distribution services during the image build.
RUN printf '#!/bin/sh\nexit 101\n' > /usr/sbin/policy-rc.d \
    && apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends mysql-server-8.0 redis-server python3 \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /build/target/mall-demo.jar /app/mall-demo.jar
COPY runtime /app/runtime
# Databases bind only to loopback; Render exposes the Java PORT, never MySQL/Redis.
ENTRYPOINT ["python3", "/app/runtime/start.py"]
