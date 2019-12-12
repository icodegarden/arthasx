FROM openjdk:8-jdk-slim

WORKDIR /opt

ADD target/arthasx-controller.jar arthasx-controller.jar
ADD arthas-tunnel-server-3.1.4.jar arthas-tunnel-server-3.1.4.jar

ENV JAVA_OPTS ""
ENV TUNNEL_SERVER_JAVA_OPTS ""

#controller web
EXPOSE 8080
#tunnel web
EXPOSE 8081
#tunnel server socket
EXPOSE 7777

CMD java $JAVA_OPTS -jar arthasx-controller.jar & java -Dserver.port=8081 $TUNNEL_SERVER_JAVA_OPTS -jar arthas-tunnel-server-3.1.4.jar
