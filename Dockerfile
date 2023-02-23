FROM openjdk:11.0.10-jre-slim-buster
WORKDIR /home/user
CMD ["./mvnw", "clean", "package"]
ARG JAR_FILE_PATH=./build/libs/*.jar
COPY ${JAR_FILE_PATH} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
