# Gym CRM App

## Project Overview
This application is a **Gym CRM system** designed to manage user profiles, training sessions, and training types. It supports different user roles, including trainees and trainers, and helps track training information. The system initializes data from CSV files, processing it to set up user accounts, training schedules, and types of workouts. For each user, the application automatically generates usernames and passwords. Additionally, it logs important actions and provides robust error handling to ensure smooth operation.

## Prerequisites

To run this application, make sure you have the following installed:

- **Java Development Kit (JDK) 17**
- **Gradle** (compatible with Spring Boot 3.3.3)
- **Git** (for cloning the project repository)

## How to Install

1. Clone the project repository:
  ```bash
   git clone https://github.com/Y-Maxym/gym-crm-app.git
  ```

2. Clone the project repository:
```bash
cd gym-crm-app
```

3. Build the project using Gradle:
```bash
./gradlew build
```

## Setup Instructions

If you have **Docker** installed, run the `docker-compose.yml` file to create a container and start the PostgreSQL database before starting the application. Use the following command:

```bash
docker-compose up -d
```

If you do not have Docker, connect to any PostgreSQL database and run the following script to create the database and add a user:
```sql
CREATE DATABASE "gym-crm";
CREATE USER gca WITH PASSWORD 'gca';
GRANT ALL PRIVILEGES ON DATABASE "gym-crm" TO gca;
```

## Run Application

To start the application locally, use the following command:
```bash
./gradlew bootRun
```

- **Java 17**
- **Spring Boot 3.3.3**
- **Gradle** (for project management)
- **PostgreSQL** (database)
- **Testcontainers** (for integration testing)
- **Liquibase** (for database migrations)
- **Docker Compose** (for setting up the database)
- **AOP (Aspect-Oriented Programming)** for logging
- **Facade Pattern** for combining service logic
- **Custom Exceptions** for error handling
- **JUnit & Mockito** for testing
- **Jacoco** for test coverage reporting

## Tests

The application is fully covered with unit tests, using **JUnit** and **Mockito** to ensure the functionality of all components. Additionally, **Testcontainers** is used for testing repository layers with an isolated PostgreSQL database.

> **Note**: To run repository tests, you need to have **Docker** installed and running, as the tests will use Docker containers to create an isolated environment for the database.

- **Line Coverage**: ![Coverage](gym-crm-service/.github/badges/jacoco.svg)
- **Branch Coverage**: ![Branches](gym-crm-service/.github/badges/branches.svg)

## Testing with Postman

You can use Postman to test the API endpoints of this application. A Postman collection is provided for your convenience.

### Postman Collection

The Postman collection can be found in the `src/test/resources` directory, specifically in the file [Postman Collection](src/test/resources/gym-crm-app.postman_collection.json). To use it:

1. Open Postman.
2. Click on "Import" in the upper left corner.
3. Select "Upload Files" and choose the Postman collection file from `src/test/resources`.
4. Once imported, you can run the requests in the collection to test the API.

Feel free to modify the requests as needed for your testing purposes.

## License
This project is open-source and available under the [MIT License](../LICENSE).