FROM openjdk:17

WORKDIR /app

COPY build/libs/pageflow-0.1.jar /app/pageflow.jar

EXPOSE 80

CMD ["java", "-jar", "/app/pageflow.jar", "--spring.profiles.active=local"]
