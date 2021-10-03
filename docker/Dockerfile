FROM softrackkz/centos-jdk:8-251
MAINTAINER Aigerim Mussina <amussina@innoforce.kz>
RUN mkdir /files
WORKDIR /
ARG IMAGE_NAME
ADD target/${IMAGE_NAME}.jar app.jar
ADD /config /config
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS="-XX:MaxRAMPercentage=80.0 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=32562 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.local.only=false"
CMD [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom  -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=18808,suspend=n -jar /app.jar" ]