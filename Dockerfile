FROM openjdk:16
EXPOSE 8080

ARG JAR_FILE=build/libs/*-all.jar
ADD ${JAR_FILE} app.jar

CMD java -jar /app.jar
