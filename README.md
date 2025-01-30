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
   docker compose build
   docker compose up -d
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
      
## Running Cucumber Tests with Tag Filtering

Our project organizes Cucumber tests using tags to allow for easy filtering and execution of specific test groups. We primarily use two tags: `@PositiveCase` for tests that should pass under normal conditions, and `@NegativeCase` for tests that are designed to fail or check for error conditions. This tagging strategy facilitates targeted test runs, enabling more efficient testing workflows.

To run a subset of tests marked with specific tags, you can use the following command in the console:

For tests marked with the `@PositiveCase` tag:

```sh
  ./gradlew :module-name:test -PcucumberTags="@PositiveCase"
```

And for tests marked with the `@NegativeCase` tag:

```sh
./gradlew :module-name:test -PcucumberTags="@NegativeCase"
```

Replace :module-name with the name of your module. These commands enable you to run all tests associated with the specified tag, making it convenient to focus on specific areas of your application during testing.
