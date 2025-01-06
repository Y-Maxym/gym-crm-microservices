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

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /usr/app/admin-service/build/libs/*.jar /app/admin-service.jar
EXPOSE 8090
CMD ["java", "-jar", "admin-service.jar"]