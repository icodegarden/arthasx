FROM openjdk:8-jdk-slim

WORKDIR /opt

ADD target/arthasx-sample-k8s.jar arthasx-sample-k8s.jar

ENV JAVA_OPTS ""

CMD java $JAVA_OPTS -jar arthasx-sample-k8s.jar
