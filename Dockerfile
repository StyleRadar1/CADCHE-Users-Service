# ==============================================================================
# STYLE RADAR - Dockerfile multi-etapa de ms-usuarios (:8120) para uso local
# El pipeline usa Dockerfile.runtime con el JAR ya validado.
# ==============================================================================

# Etapa 1: compilación con el Maven wrapper
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY src ./src
RUN ./mvnw -B clean package -DskipTests

# Etapa 2: ejecución liviana y sin privilegios
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# CA pública del broker compartido para conexiones AMQPS.
COPY rabbitmq-ca.pem /tmp/rabbitmq-ca.pem
RUN keytool -importcert -trustcacerts -noprompt \
    -alias styleradar-rabbitmq-root-ca \
    -file /tmp/rabbitmq-ca.pem \
    -cacerts -storepass changeit \
    && rm /tmp/rabbitmq-ca.pem

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder --chown=appuser:appgroup /build/target/*.jar app.jar

ENV TZ=America/Bogota

EXPOSE 8120

HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=5 \
  CMD wget -q -O /dev/null http://localhost:8120/actuator/health || exit 1

USER appuser:appgroup

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:InitialRAMPercentage=50.0", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-jar", "app.jar"]
