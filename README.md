# Gym CRM Microservices


This is a microservices project built with Spring Boot.

## Overview

### Services

- **discovery-service**: This is a Eureka service responsible for registering other services in the network and providing them with names.
- **gateway-service**: This service acts as a common access point for the other services.
- **authentication-service**: This service handles authentication using JWT tokens. It verifies user credentials and generates JWT tokens. Every request sent to the gateway is sent to the authentication service to validate the token before being forwarded to the initial destination.
- **gym-crm-service**: The main application where the core business logic resides. It publishes messages to a message broker (ActiveMQ) when a training session is created.
- **trainer-hours-service**: This auxiliary service consumes messages from ActiveMQ, converts them to entities and stores them in MongoDB. It also has endpoints for retrieving data from MongoDB.
- **admin-service**: A service that acts as an admin dashboard for the other services, implemented using Spring Boot Admin.
- **config-service**: A Spring Boot Config implementation for a shared configuration across services.

### Additional Technologies

- **PostgreSQL**: Used by the `gym-crm-service`.
- **Prometheus**: Also used by the `gym-crm-service` for monitoring.
- **ActiveMQ**: A message broker used by the `gym-crm-service` to publish messages and `trainer-hours-service` to consume and process messages, including DLQ (Dead Letter Queue) messages.
- **MongoDB**: Used by the `trainer-hours-service` to store and retrieve data.
- **Docker**: Used to containerize all ancillary services.

All applications utilize a RESTful architecture.

## UML Diagram

Below is a UML diagram that illustrates the architecture of the microservices:

![UML Diagram](.github/Microservices%20Diagram.png)


## Prerequisites

To run this project, you need:

- **Java 17**
- **Docker**

## Running the Project

1. Ensure you have Java 17 installed on your system.
2. Ensure Docker is installed and running on your system.
3. Use the provided `docker-compose` file to deploy the ancillary services:
   ```sh
   docker-compose up
   ```
4. Start each microservice individually:
    - Navigate to the directory of each microservice.
    - Build the microservice using Maven or Gradle:
      ```sh
      ./mvnw clean install
      ```
      или
      ```sh
      ./gradlew clean build
      ```
    - Run the microservice:
      ```sh
      java -jar target/your-microservice.jar
      ```
      или, если используете Gradle,
      ```sh
      ./gradlew bootRun
      ```