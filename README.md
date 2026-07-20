# Red Hat Enterprise DevOps Platform

A containerized enterprise bookstore platform built to demonstrate modern DevOps, backend engineering, containerization, database integration, and Red Hat ecosystem technologies.

The project is being developed as a practical demonstration of how a Java Spring Boot application can be developed, tested, containerized, and connected to a PostgreSQL database using Podman.

---

## Project Status

🚧 **In Development**

### Completed

* Spring Boot REST API
* Java 21 application runtime
* PostgreSQL 16 database
* Spring Data JPA and Hibernate integration
* CRUD operations for books
* Podman containerization
* Container-to-database communication
* Environment-based database configuration
* Multi-container local development environment

### Planned

* Podman Compose orchestration
* Persistent PostgreSQL volumes
* Health checks
* Automated unit and integration testing
* CI/CD pipeline
* Container image scanning
* Kubernetes deployment
* OpenShift deployment
* Prometheus metrics
* Grafana dashboards
* Centralized logging
* Security improvements
* Infrastructure as Code

---

# Architecture

```text
                         Developer Machine
                                │
                                ▼
                        ┌─────────────────┐
                        │  Podman Machine  │
                        │   Linux / ARM64 │
                        └────────┬────────┘
                                 │
                 ┌───────────────┴────────────────┐
                 │                                │
                 ▼                                ▼
        ┌─────────────────┐              ┌─────────────────────┐
        │  bookstore-app  │              │ bookstore-postgres  │
        │                 │              │                     │
        │  Spring Boot   │              │    PostgreSQL 16    │
        │  Java 21       │              │                     │
        │  Port: 8080    │              │    Port: 5432        │
        └────────┬────────┘              └──────────┬──────────┘
                 │                                  │
                 └──────────────┬───────────────────┘
                                │
                                ▼
                          Books Database
```

---

# Technology Stack

## Backend

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Hibernate ORM

## Database

* PostgreSQL 16

## Containerization

* Podman
* Podman Machine
* Rootless Containers
* Netavark
* Aardvark DNS
* crun

## Build Tool

* Apache Maven

## Development Environment

* IntelliJ IDEA
* macOS
* Apple Silicon / ARM64

---

# Project Structure

```text
redhat-enterprise-devops-platform/
│
├── application/
│   │
│   └── bookstore/
│       │
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/
│       │   │   │   └── com/
│       │   │   │       └── redhatdevops/
│       │   │   │           └── bookstore/
│       │   │   │               │
│       │   │   │               ├── BookstoreApplication.java
│       │   │   │               │
│       │   │   │               ├── controller/
│       │   │   │               │   └── BookController.java
│       │   │   │               │
│       │   │   │               ├── entity/
│       │   │   │               │   └── Book.java
│       │   │   │               │
│       │   │   │               ├── repository/
│       │   │   │               │   └── BookRepository.java
│       │   │   │               │
│       │   │   │               └── service/
│       │   │   │                   └── BookService.java
│       │   │   │
│       │   │   └── resources/
│       │   │       └── application.properties
│       │   │
│       │   └── test/
│       │
│       ├── Dockerfile
│       ├── pom.xml
│       ├── mvnw
│       └── mvnw.cmd
│
└── README.md
```

---

# Application Architecture

The application follows a layered architecture:

```text
Client
  │
  ▼
BookController
  │
  ▼
BookService
  │
  ▼
BookRepository
  │
  ▼
PostgreSQL
```

## Controller Layer

Handles HTTP requests and exposes the REST API.

```text
/api/books
```

## Service Layer

Contains business logic and coordinates application operations.

## Repository Layer

Uses Spring Data JPA to communicate with PostgreSQL.

## Entity Layer

Defines the database model.

---

# Book Entity

The current `Book` entity contains:

| Field  | Type   |
| ------ | ------ |
| id     | Long   |
| title  | String |
| author | String |
| price  | double |

The entity is mapped to the PostgreSQL table:

```text
books
```

---

# REST API

## Get All Books

```http
GET /api/books
```

Example:

```bash
curl http://localhost:8080/api/books
```

Response:

```json
[]
```

---

## Get a Book

```http
GET /api/books/{id}
```

Example:

```bash
curl http://localhost:8080/api/books/1
```

---

## Create a Book

```http
POST /api/books
```

Example:

```bash
curl -X POST http://localhost:8080/api/books \
-H "Content-Type: application/json" \
-d '{
  "title": "The DevOps Handbook",
  "author": "Gene Kim",
  "price": 45.99
}'
```

---

## Update a Book

```http
PUT /api/books/{id}
```

Example:

```bash
curl -X PUT http://localhost:8080/api/books/1 \
-H "Content-Type: application/json" \
-d '{
  "title": "The DevOps Handbook - Updated",
  "author": "Gene Kim",
  "price": 50.00
}'
```

---

## Delete a Book

```http
DELETE /api/books/{id}
```

Example:

```bash
curl -X DELETE http://localhost:8080/api/books/1
```

---

# Database Configuration

The application connects to PostgreSQL using environment variables when running inside Podman:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
```

Example:

```bash
-e SPRING_DATASOURCE_URL=jdbc:postgresql://host.containers.internal:5432/bookstore
-e SPRING_DATASOURCE_USERNAME=bookstore
-e SPRING_DATASOURCE_PASSWORD=bookstore
```

This avoids hardcoding environment-specific database configuration into the container image.

---

# Running the Database

The PostgreSQL container is created using:

```bash
podman run -d \
  --name bookstore-postgres \
  -e POSTGRES_DB=bookstore \
  -e POSTGRES_USER=bookstore \
  -e POSTGRES_PASSWORD=bookstore \
  -p 5432:5432 \
  docker.io/library/postgres:16
```

Check the running containers:

```bash
podman ps
```

Expected:

```text
bookstore-postgres
```

---

# Building the Application

Navigate to the application:

```bash
cd application/bookstore
```

Run the tests:

```bash
./mvnw clean test
```

Build the application:

```bash
./mvnw clean package -DskipTests
```

This generates a Spring Boot JAR inside:

```text
target/
```

---

# Containerizing the Application

The application uses a `Dockerfile`:

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/bookstore-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build the image:

```bash
podman build -t bookstore:1.0 .
```

Run the application container:

```bash
podman run -d \
  --name bookstore-app \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.containers.internal:5432/bookstore \
  -e SPRING_DATASOURCE_USERNAME=bookstore \
  -e SPRING_DATASOURCE_PASSWORD=bookstore \
  bookstore:1.0
```

Check the containers:

```bash
podman ps
```

Expected:

```text
bookstore-app
bookstore-postgres
```

---

# Current Containerized Architecture

```text
┌─────────────────────────────────────────────┐
│              Podman Environment             │
│                                             │
│  ┌──────────────────┐                       │
│  │  bookstore-app   │                       │
│  │                  │                       │
│  │  Spring Boot     │                       │
│  │  Java 21         │                       │
│  │  Port 8080       │                       │
│  └────────┬─────────┘                       │
│           │                                 │
│           │ JDBC                            │
│           ▼                                 │
│  ┌──────────────────┐                       │
│  │ bookstore-postgres│                      │
│  │                  │                       │
│  │ PostgreSQL 16    │                       │
│  │ Port 5432        │                       │
│  └──────────────────┘                       │
│                                             │
└─────────────────────────────────────────────┘
```

---

# Health Check

The application exposes Spring Boot Actuator endpoints.

Health endpoint:

```http
GET /actuator/health
```

Test:

```bash
curl http://localhost:8080/actuator/health
```

Expected:

```json
{
  "status": "UP"
}
```

---

# DevOps Engineering Practices Demonstrated

This project currently demonstrates:

* Layered Spring Boot architecture
* REST API development
* Database persistence
* Containerized application runtime
* Containerized database
* Environment-based configuration
* Rootless Podman containers
* Linux container runtime concepts
* Container networking
* PostgreSQL integration
* Maven build automation
* Java 21 runtime management
* Application health monitoring

---

# Roadmap

## Phase 1 — Application Development

* [x] Create Spring Boot application
* [x] Add PostgreSQL database
* [x] Create Book entity
* [x] Create Repository layer
* [x] Create Service layer
* [x] Create REST Controller
* [x] Implement CRUD operations

## Phase 2 — Containerization

* [x] Install Podman
* [x] Configure Podman Machine
* [x] Run PostgreSQL using Podman
* [x] Create application Dockerfile
* [x] Build application container image
* [x] Run Spring Boot in a container
* [x] Connect application container to PostgreSQL

## Phase 3 — Container Orchestration

* [ ] Create `compose.yaml`
* [ ] Add PostgreSQL persistent volumes
* [ ] Add container health checks
* [ ] Configure container networks
* [ ] Add environment files
* [ ] Add container startup dependencies

## Phase 4 — CI/CD

* [ ] GitHub Actions pipeline
* [ ] Automated Maven tests
* [ ] Build container image automatically
* [ ] Container image security scanning
* [ ] Push image to a container registry
* [ ] Automated deployment

## Phase 5 — Kubernetes / OpenShift

* [ ] Create Kubernetes manifests
* [ ] Deploy application to Kubernetes
* [ ] Create Deployment
* [ ] Create Service
* [ ] Configure ConfigMaps
* [ ] Configure Secrets
* [ ] Deploy to OpenShift
* [ ] Create OpenShift Routes

## Phase 6 — Observability

* [ ] Prometheus metrics
* [ ] Grafana dashboards
* [ ] Centralized logging
* [ ] Application performance monitoring
* [ ] Health monitoring
* [ ] Alerting

## Phase 7 — Security

* [ ] Non-root container execution
* [ ] Container image scanning
* [ ] Secrets management
* [ ] Dependency vulnerability scanning
* [ ] API authentication
* [ ] Role-based access control
* [ ] Security-focused CI/CD gates

---

# Project Goals

The long-term goal of this project is to build a complete enterprise-style platform demonstrating the complete software delivery lifecycle:

```text
Developer
    │
    ▼
Source Code
    │
    ▼
Maven Build
    │
    ▼
Automated Tests
    │
    ▼
Container Image
    │
    ▼
Security Scan
    │
    ▼
Container Registry
    │
    ▼
Kubernetes / OpenShift
    │
    ▼
Monitoring & Logging
    │
    ▼
Production Deployment
```

---

## Author

**Jabelo Pitso**

Software Engineer | DevOps Engineer | Cloud & Backend Development

Focused on:

* Java
* Spring Boot
* DevOps
* Cloud Engineering
* Containerization
* Kubernetes
* OpenShift
* CI/CD
* Azure
* Red Hat Technologies

---

> This project is actively being developed as a practical demonstration of enterprise DevOps engineering and cloud-native application delivery.
