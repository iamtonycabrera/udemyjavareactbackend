FROM openjdk:11-jre-slim
RUN useradd -ms /bin/bash springuser
USER springuser
WORKDIR /home/springuser
ADD ./target/app.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
