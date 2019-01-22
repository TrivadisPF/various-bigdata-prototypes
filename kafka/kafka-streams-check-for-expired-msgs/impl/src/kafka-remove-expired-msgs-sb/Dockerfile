FROM anapsix/alpine-java

VOLUME /tmp

ARG JAR_FILE
COPY target/kafka-remove-expired-msgs-sb-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]