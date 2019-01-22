FROM fedora:27

RUN yum -y update && yum -y install java-1.8.0-openjdk-devel && yum -y clean all
ENV JAVA_HOME /usr/lib/jvm/java

VOLUME /tmp

ARG JAR_FILE
COPY target/kafka-remove-expired-msgs-sb-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]