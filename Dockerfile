FROM eclipse-temurin:20 as build

WORKDIR /tmp/build
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .
COPY src src
RUN chmod +x gradlew && ./gradlew shadowJar

FROM eclipse-temurin:20
WORKDIR /app

RUN apt-get update && apt-get install -y netcat

# Copiamos el jar y el archivo de entorno
COPY --from=build /tmp/build/build/libs/kotlin-htmx-all.jar .
COPY .env.default .

# Copiamos el script que espera a PostgreSQL
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh

EXPOSE 8080
ENV TZ="America/Guatemala"

# Usamos el script para esperar que DB esté lista
ENTRYPOINT ["/wait-for.sh", "db", "5432", "--"]
CMD ["java", "-jar", "kotlin-htmx-all.jar"]
