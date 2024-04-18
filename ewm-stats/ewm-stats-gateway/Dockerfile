FROM amazoncorretto:11
COPY target/*.jar app.jar
#ENV TZ="Europe/Moscow"
ENTRYPOINT ["sh","-c","java ${JAVA_OPTS} -jar /app.jar"]