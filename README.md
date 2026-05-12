# Team Task Manager

A full-stack project management application for teams to organize projects, assign tasks, track progress, and manage work through role-based project access.

The project includes a Spring Boot REST API, an Angular frontend, JWT authentication, MySQL persistence, Swagger documentation, and seed data for quick local testing.

## Live Backend

```text
API: https://teamtaskmanager-production-7c0a.up.railway.app/api
Swagger: https://teamtaskmanager-production-7c0a.up.railway.app/swagger-ui.html
```

## What The App Does

- User signup and login
- JWT-based authentication
- Personal dashboard with project and task summaries
- Project creation and project detail views
- Project member management
- Project-level roles: `ADMIN` and `MEMBER`
- Task creation, assignment, editing, deletion, and status updates
- "My Tasks" page for assigned work
- Project progress tracking
- Validation and structured API error responses
- Swagger UI for backend API testing

## Tech Stack

### Backend

- Java 17
- Spring Boot 3
- Spring Security
- JWT authentication with `jjwt`
- Spring Data JPA / Hibernate
- MySQL
- Jakarta Validation
- Springdoc OpenAPI / Swagger
- Maven

### Frontend

- Angular 17
- TypeScript
- Angular Router
- Reactive Forms
- HttpClient
- Standalone Angular components

## Repository Structure

```text
TeamTaskManager/
  backend/
    src/main/java/com/teamtaskmanager/
      config/
      controller/
      dto/
      entity/
      enums/
      exception/
      repository/
      security/
      service/
    src/main/resources/application.yml
    pom.xml

  frontend/
    src/app/
      core/
      models/
      pages/
      services/
    src/environments/
    package.json
```

## Main Features

### Authentication

Users can create an account and log in with email and password. Passwords are hashed with BCrypt, and authenticated requests use a bearer token.

```http
Authorization: Bearer <jwt-token>
```

### Projects

Authenticated users can create projects, view projects they belong to, open project details, and manage project membership when they are the project admin.

The user who creates a project automatically becomes that project's `ADMIN`.

### Project Access Control

Access is handled at the project level:

| Capability | ADMIN | MEMBER |
|---|---:|---:|
| View project | Yes | Yes |
| View project tasks | Yes | Yes |
| Create tasks | Yes | No |
| Edit tasks | Yes | No |
| Delete tasks | Yes | No |
| Add or remove members | Yes | No |
| Update assigned task status | Yes | Yes, for own assigned tasks |
| Delete project | Yes | No |

### Tasks

Tasks belong to projects and can include:

- title
- description
- status
- priority
- due date
- assignee
- project reference

Supported statuses and priorities are defined as enums in the backend.

### Dashboard

The dashboard provides a quick view of user work, including task counts, overdue work, assigned tasks, and project progress.

## API Overview

Base URL:

```text
http://localhost:8080/api
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

### Auth

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/signup` | Register a new user |
| POST | `/api/auth/login` | Log in and receive a JWT |

### Users

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users/me` | Get the current authenticated user |

### Projects

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/projects` | Create a project |
| GET | `/api/projects` | List projects for the current user |
| GET | `/api/projects/{projectId}` | Get project details |
| POST | `/api/projects/{projectId}/members` | Add a member to a project |
| DELETE | `/api/projects/{projectId}/members/{userId}` | Remove a project member |
| DELETE | `/api/projects/{projectId}` | Delete a project |

### Tasks

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/projects/{projectId}/tasks` | Create a task in a project |
| GET | `/api/projects/{projectId}/tasks` | List tasks in a project |
| GET | `/api/tasks/my` | List tasks assigned to the current user |
| GET | `/api/tasks/{taskId}` | Get one task |
| PUT | `/api/tasks/{taskId}` | Update a task |
| PATCH | `/api/tasks/{taskId}/status` | Update task status |
| DELETE | `/api/tasks/{taskId}` | Delete a task |

### Dashboard

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/dashboard` | Current user's dashboard summary |
| GET | `/api/projects/{projectId}/dashboard` | Project progress dashboard |

## Local Setup

### Prerequisites

- Java 17 or newer
- Maven
- Node.js and npm
- MySQL

### 1. Clone The Repository

```bash
git clone <your-repository-url>
cd TeamTaskManager
```

### 2. Configure The Backend

The backend reads configuration from environment variables, with defaults in:

```text
backend/src/main/resources/application.yml
```

Default local database:

```text
jdbc:mysql://localhost:3306/team_task_manager?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

Create the database manually if needed:

```sql
CREATE DATABASE team_task_manager;
```

Common backend environment variables:

```env
PORT=8080
DB_URL=jdbc:mysql://localhost:3306/team_task_manager?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
DB_DRIVER=com.mysql.cj.jdbc.Driver
DDL_AUTO=update
CORS_ALLOWED_ORIGIN=http://localhost:4200
JWT_SECRET=replace-with-a-long-secret-key
JWT_EXPIRATION_MS=86400000
SEED_DATA_ENABLED=true
```

### 3. Start The Backend

```bash
cd backend
mvn spring-boot:run
```

Backend URLs:

```text
API: http://localhost:8080
Swagger: http://localhost:8080/swagger-ui.html
```

### 4. Start The Frontend

Open a second terminal:

```bash
cd frontend
npm install
npm start
```

Frontend URL:

```text
http://localhost:4200
```

The Angular app calls the backend at:

```text
http://localhost:8080/api
```

## Demo Accounts

Seed data is enabled by default for easier testing.

```text
Admin user
Email: admin@teamtaskmanager.com
Password: Admin@12345

Member user
Email: member@teamtaskmanager.com
Password: Member@12345
```

## Example Requests

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@teamtaskmanager.com","password":"Admin@12345"}'
```

### List Projects

```bash
curl http://localhost:8080/api/projects \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Create Project

```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Website Redesign","description":"Refresh the company website"}'
```

### Add Project Member

```bash
curl -X POST http://localhost:8080/api/projects/1/members \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"email":"member@teamtaskmanager.com","role":"MEMBER"}'
```

### Create Task

```bash
curl -X POST http://localhost:8080/api/projects/1/tasks \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Create wireframes","description":"Prepare dashboard wireframes","priority":"HIGH","dueDate":"2026-05-20","assigneeId":2}'
```

### Update Task Status

```bash
curl -X PATCH http://localhost:8080/api/tasks/1/status \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status":"IN_PROGRESS"}'
```

## Error Handling

The backend uses validation annotations, custom exceptions, and a global exception handler to return predictable JSON errors for invalid requests, duplicate data, forbidden actions, and missing resources.

Example error shape:

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "You do not have permission to perform this action",
  "path": "/api/projects/1/tasks"
}
```

## Frontend Configuration

Local API configuration:

```text
frontend/src/environments/environment.ts
```

Production API configuration:

```text
frontend/src/environments/environment.prod.ts
```

Update the production API URL before deploying the frontend:

```ts
export const environment = {
  production: true,
  apiUrl: 'https://your-backend-url.com/api'
};
```

## Build Commands

Backend:

```bash
cd backend
mvn clean package
```

Frontend:

```bash
cd frontend
npm run build
```

Angular build output:

```text
frontend/dist/team-task-manager-frontend
```

## Deployment Notes

The backend includes Railway deployment files:

- `backend/railway.json`
- `backend/Procfile`

Current deployed backend:

```text
https://teamtaskmanager-production-7c0a.up.railway.app
```

For deployment, set production environment variables for the database, JWT secret, CORS origin, and seed-data behavior.

Recommended production settings:

```env
DDL_AUTO=update
SEED_DATA_ENABLED=false
JWT_SECRET=use-a-long-secure-production-secret
CORS_ALLOWED_ORIGIN=https://your-frontend-domain.com
```

The frontend can be deployed to any static hosting provider such as Vercel, Netlify, Railway static hosting, or similar platforms.

## Design Decisions

- JWT authentication keeps the API stateless and simple to consume from Angular.
- Project roles are stored per project, so users can be an admin in one project and a member in another.
- Authorization checks are enforced in the service layer where business rules are applied.
- DTOs keep API input and output separate from database entities.
- Seed data is included to make reviewer testing faster.
- MySQL is used as the main relational database because projects, members, and tasks map naturally to relational tables.

## Verification

The application can be verified through the Angular UI, Swagger, or curl by checking:

- user signup and login
- JWT-protected route access
- project creation
- member addition and removal
- admin-only task creation and deletion
- member task status updates
- dashboard summary data
- validation and permission error responses
