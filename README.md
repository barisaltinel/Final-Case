# Task Management Application

## Overview
This project is a **Task Management System** built using **Spring Boot**. It provides role-based access control and allows users to manage tasks, projects, attachments, and comments with different authorization levels. The application also implements **soft delete**, **file upload**, and **security mechanisms** with **Spring Security**.

## Features
- **User Management:** Role-based authentication and authorization
- **Project Management:** Create, update, and delete projects
- **Task Management:** Assign, update, and cancel tasks
- **Attachment Management:** Upload and delete attachments
- **Comment System:** Add comments to tasks
- **Security:** Role-based access control (RBAC) with Spring Security
- **Database Support:** MySQL for production, H2 for testing
- **Soft Delete Support:** Entities are logically deleted rather than being permanently removed

## Technologies Used
- **Java 21**
- **Spring Boot 3.3.9**
- **Spring Security**
- **Spring Data JPA**
- **MySQL (Production) / H2 (Testing)**
- **Lombok**
- **Maven**

## Project Structure
```
|-- src
|   |-- main
|   |   |-- java/com/patika/bootcamp/taskmanagement
|   |   |   |-- config (Security Configurations)
|   |   |   |-- controller (REST APIs)
|   |   |   |-- model (Entities)
|   |   |   |-- repository (JPA Repositories)
|   |   |   |-- service (Business Logic)
|   |   |   |-- util (Helper Classes)
|   |   |-- resources
|   |   |   |-- application.properties
|-- pom.xml
|-- README.md
```

## Setup and Installation
### Prerequisites
- Install **Java 21**
- Install **Maven**
- Install **MySQL** (if running in production mode)

### Clone the Repository
```sh
git clone https://github.com/your-repo/taskmanagement.git
cd taskmanagement
```

### Configure Database
Modify `application.properties` (for development use H2, for production use MySQL):
```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/taskmanagement?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

### Run the Application
```sh
mvn spring-boot:run
```

### Access the Application
- **H2 Console (Testing Only):** `http://localhost:8080/h2-console`
- **API Endpoints:** `http://localhost:8080/api/*`

## API Endpoints
### Authentication
| Method | Endpoint | Access |
|--------|---------|--------|
| `POST` | `/api/auth/register` | Open |

### Project Management
| Method | Endpoint | Access |
|--------|---------|--------|
| `GET` | `/api/projects` | PROJECT_MANAGER, ADMIN |
| `POST` | `/api/projects` | PROJECT_MANAGER, ADMIN |
| `PUT` | `/api/projects/{id}` | PROJECT_MANAGER, ADMIN |
| `DELETE` | `/api/projects/{id}` | ADMIN |

### Task Management
| Method | Endpoint | Access |
|--------|---------|--------|
| `GET` | `/api/tasks` | All Authenticated Users |
| `POST` | `/api/tasks` | PROJECT_MANAGER, ADMIN |
| `PUT` | `/api/tasks/{id}` | PROJECT_MANAGER, TEAM_LEADER, ADMIN |
| `PUT` | `/api/tasks/{id}/cancel` | PROJECT_MANAGER, ADMIN |

### Attachment Management
| Method | Endpoint | Access |
|--------|---------|--------|
| `POST` | `/api/attachments` | TEAM_MEMBER, TEAM_LEADER, PROJECT_MANAGER, ADMIN |
| `DELETE` | `/api/attachments/{id}` | TEAM_MEMBER, TEAM_LEADER, PROJECT_MANAGER, ADMIN |

### User Management
| Method | Endpoint | Access |
|--------|---------|--------|
| `GET` | `/api/users` | ADMIN |
| `PUT` | `/api/users/{id}` | ADMIN, User Owner |
| `DELETE` | `/api/users/{id}` | ADMIN |

## Security Roles
| Role | Permissions |
|------|------------|
| `ADMIN` | Full Access |
| `PROJECT_MANAGER` | Manage Projects & Tasks |
| `TEAM_LEADER` | Manage Tasks |
| `TEAM_MEMBER` | Limited Task Access |

## Testing
Run the tests using:
```sh
mvn test
```
