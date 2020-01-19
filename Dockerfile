FROM maven:3.6.1-jdk-12 AS build

LABEL maintainer=CoderNoOne

ARG MYSQL_USER
ARG MYSQL_PASSWORD
ARG MYSQL_DATABASE
ARG MYSQL_SERVICE

COPY / /

RUN cd connection && mvn clean prepare-package \
 -DMYSQL_USER=${MYSQL_USER} \
 -DMYSQL_PASSWORD=${MYSQL_PASSWORD} \
 -DMYSQL_DATABASE=${MYSQL_DATABASE} \
 -DMYSQL_SERVICE=${MYSQL_SERVICE} \
 -DskipTests

RUN cd .. && mvn clean package -DskipTests

ENTRYPOINT ["sh", "-c", "java --enable-preview -jar main/target/main-uber.jar"]




