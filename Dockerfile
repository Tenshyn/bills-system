FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

# Copy maven files first for better layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Make the mvnw script executable
RUN chmod +x ./mvnw

# Build a release JAR
RUN ./mvnw package -DskipTests

# Extract the layers to optimize Docker caching
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Production stage
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp

# Copy the exploded layers for caching to work effectively
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "tenshy.bills.BillsApplication"]