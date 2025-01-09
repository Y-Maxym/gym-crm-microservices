FROM gradle:8.5.0-jdk17 AS build
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY build.gradle settings.gradle $APP_HOME

COPY gradle $APP_HOME/gradle
COPY --chown=gradle:gradle . /home/gradle/src
USER root
RUN chown -R gradle /home/gradle/src

RUN gradle build || return 0
COPY . .
RUN gradle clean build -x test

FROM openjdk:24-ea-17-jdk-slim
WORKDIR /app
ENV DOCKERIZE_VERSION v0.8.0
RUN apt-get update \
    && apt-get install -y wget \
    && wget -O - https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz | tar xzf - -C /usr/local/bin \
    && apt-get autoremove -yqq --purge wget && rm -rf /var/lib/apt/lists/*
COPY --from=build /usr/app/authentication-service/build/libs/*.jar /app/authentication-service.jar
EXPOSE 8083
CMD dockerize \
    -wait tcp://eureka-service:8761 \
    -wait tcp://postgres-db:5432 \
    -wait tcp://gym-crm-service:8081 \
    -timeout 600s \
    java -jar authentication-service.jar