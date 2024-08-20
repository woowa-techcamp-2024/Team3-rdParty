FROM gradle:7.6.1-jdk17-jammy

#RUN yum install -y curl

WORKDIR /app

# Copy the Gradle configuration files and source code
COPY gradlew /app/
COPY gradle /app/gradle
COPY build.gradle.kts /app/
COPY settings.gradle.kts /app/
COPY backend-config /app/backend-config
COPY src /app/src

RUN rm -f /app/src/test/resources/application.yml

# Make the Gradle wrapper executable
RUN chmod +x ./gradlew

# Copy test resources

# run test
RUN ./gradlew copyYmlTest
#RUN cat /app/src/test/resources/application.yml
#RUN ./gradlew test
ENTRYPOINT ["./gradlew", "test"]
#ENTRYPOINT ["tail", "-f", "/dev/null"]
