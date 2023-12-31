# TravelLog Application

This is a Spring Boot application for managing travel logs.

## Prerequisites
- Java 17 or higher (As 14 is not LTS I used Java 17)
- PostgreSQL.
    If you use docker container, here is the command: 
````
docker run -d -p 5432:5432 --name postgres15 -e POSTGRES_USER=username -e POSTGRES_PASSWORD=password -e POSTGRES_DB=travel_log_db postgres:15
````

## Installation
1. Clone this repository.
2. Open the project in your preferred IDE.
3. Update the database connection details in `application.yaml`.
4. Run the application.

## Usage
- Access the API documentation at http://localhost:8080/swagger-ui.html.
- Use the provided endpoints to manage travel logs.

## Logging
Logs are generated by the application to provide insights into its behavior and diagnose issues. They are stored in the logs directory in the application's root folder.


Log files are saved in the following format: application.{yyyy-MM-dd}.log. Old log files are automatically rotated to keep the most recent logs.