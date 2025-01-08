# Haircut Salon Reservations

This project is a backend application for managing reservations and services in a haircut salon. It is built using Spring Boot and provides RESTful APIs for various operations related to users, services, and reservations.

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Setup](#setup)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [License](#license)

## Features

- User authentication and authorization using JWT
- CRUD operations for users, services, and reservations
- Group reservations by date and time slots
- Swagger UI for API documentation

## Technologies

- Java 17
- Spring Boot 3.4.1
- Spring Security
- Spring Data JPA
- MySQL
- JWT (JSON Web Token)
- Lombok
- Swagger (Springdoc OpenAPI)
- Log4j2

## Setup

### Prerequisites

- Java 17
- Maven
- MySQL

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/yourusername/haircut-salon-reservations.git
    cd haircut-salon-reservations
    ```

2. Configure the database in [application.properties]:
    ```properties
    spring.datasource.url=jdbc:mysql://your-database-url:3306/your-database-name?useSSL=false&serverTimezone=UTC
    spring.datasource.username=your-database-username
    spring.datasource.password=your-database-password
    ```

3. Build the project:
    ```sh
    ./mvnw clean install
    ```

4. Run the application:
    ```sh
    ./mvnw spring-boot:run
    ```

## Usage

### Accessing the API

The API can be accessed at [http://localhost:8080/api]. You can use tools like Postman or cURL to interact with the API.

### Swagger UI

Swagger UI is available at [http://localhost:8080/swagger-ui.html] for API documentation and testing.

## API Endpoints

### Authentication

- [POST /api/auth/login] - User login
- [POST /api/auth/logout] - User logout`
- [POST /api/auth/register] - User registration

### Users

- [GET /api/users] - Get all users (Admin only)
- [GET /api/users/{id}] - Get user by ID (Admin only)
- [POST /api/users] - Create a new user (Admin only)
- [PUT /api/users/{id}] - Update user by ID (Admin only)
- [DELETE /api/users/{id}] - Delete user by ID (Admin only)

### Services

- [GET /api/services] - Get all services
- [POST /api/services] - Create a new service (Admin only)
- [PUT /api/services/{id}] - Update service by ID (Admin only)
- [DELETE /api/services/{id}] - Delete service by ID (Admin only)

### Reservations

- [GET /api/reservations] - Get all reservations
- [GET /api/reservations/calendar] - Get reservations grouped by date
- [GET /api/reservations/customer/{customerID}] - Get reservations by customer ID
- [POST /api/reservations] - Create a new reservation
- [PUT /api/reservations/{id}] - Update reservation by ID
- [DELETE /api/reservations/{id}] - Delete reservation by ID

## Configuration

### Application Properties

The application properties are configured in [application.properties] and [application-dev.properties]. You can customize the database connection, JWT settings, and other configurations.

### Logging

Logging is configured using Log4j2. The configuration file is located at [log4j2.xml].

## License

This project is licensed under the MIT License. See the LICENSE file for details.