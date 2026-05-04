# Team Task Manager Backend

Production-ready Spring Boot 3 REST API for Team Task Manager.

## Features

- Signup and login with BCrypt password hashing
- JWT authentication with stateless Spring Security
- Project creation and member management
- Project-scoped roles: `ADMIN` and `MEMBER`
- Task creation, assignment, update, status update, and deletion
- Dashboard summaries with task counts, overdue tasks, assigned tasks, and project progress
- DTO-based API responses
- Jakarta Validation request validation
- Global JSON error handling
- Swagger/OpenAPI UI
- MySQL default database with environment-variable overrides
- Demo seed data for quick API testing

## Tech Stack

- Java 17+
- Spring Boot 3
- Spring Security
- JWT via `jjwt`
- Spring Data JPA and Hibernate
- MySQL
- Springdoc OpenAPI
- Railway deployment config

## Package Structure

- `config` - CORS, seed data, runtime properties
- `controller` - REST controllers
- `dto` - request and response DTOs
- `entity` - JPA entities
- `enums` - role, task status, task priority
- `exception` - global API errors and custom exceptions
- `repository` - Spring Data repositories
- `security` - JWT, security filter chain, auth entrypoint
- `service` - business logic and authorization checks

## Local Setup

From the `backend` directory:

```bash
mvn spring-boot:run
```

The default setup uses MySQL. Make sure MySQL is running, then create the database if your JDBC URL does not use `createDatabaseIfNotExist=true`:

```sql
CREATE DATABASE team_task_manager;
```

- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- MySQL JDBC URL: `jdbc:mysql://localhost:3306/team_task_manager?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`

If Maven is not installed, install Maven or run the project from an IDE with Maven support.

## Environment Variables

```env
PORT=8080
DB_URL=jdbc:mysql://localhost:3306/team_task_manager?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=
DB_DRIVER=com.mysql.cj.jdbc.Driver
DDL_AUTO=update
CORS_ALLOWED_ORIGIN=http://localhost:4200
JWT_SECRET=replace-with-at-least-32-characters-secret-key
JWT_EXPIRATION_MS=86400000
SEED_DATA_ENABLED=true
```

## Demo Credentials

Seed data is enabled by default.

```text
Admin:
email: admin@teamtaskmanager.com
password: Admin@12345

Member:
email: member@teamtaskmanager.com
password: Member@12345
```

## API Endpoints

### Auth

- `POST /api/auth/signup`
- `POST /api/auth/login`

### Users

- `GET /api/users/me` - current authenticated user

### Projects

- `POST /api/projects` - create project, current user becomes project admin
- `GET /api/projects` - list current user's projects
- `GET /api/projects/{projectId}` - project details
- `POST /api/projects/{projectId}/members` - admin adds member by email
- `DELETE /api/projects/{projectId}/members/{userId}` - admin removes member
- `DELETE /api/projects/{projectId}` - admin deletes project

### Tasks

- `POST /api/projects/{projectId}/tasks` - admin creates task
- `GET /api/projects/{projectId}/tasks` - project member views tasks
- `GET /api/tasks/my` - current user's assigned tasks
- `GET /api/tasks/{taskId}` - project member views task
- `PUT /api/tasks/{taskId}` - admin updates task
- `PATCH /api/tasks/{taskId}/status` - admin or assignee updates status
- `DELETE /api/tasks/{taskId}` - admin deletes task

### Dashboard

- `GET /api/dashboard`
- `GET /api/projects/{projectId}/dashboard`

## Quick API Check

Login:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@teamtaskmanager.com","password":"Admin@12345"}'
```

Use the returned token:

```bash
curl http://localhost:8080/api/projects \
  -H "Authorization: Bearer YOUR_TOKEN"
```

Create a project:

```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Website Redesign","description":"Marketing site refresh"}'
```

Add a member:

```bash
curl -X POST http://localhost:8080/api/projects/1/members \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"email":"member@teamtaskmanager.com","role":"MEMBER"}'
```

Create a task:

```bash
curl -X POST http://localhost:8080/api/projects/1/tasks \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Create wireframes","description":"Initial dashboard wireframes","priority":"HIGH","dueDate":"2026-05-10","assigneeId":2}'
```

Update task status:

```bash
curl -X PATCH http://localhost:8080/api/tasks/1/status \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status":"IN_PROGRESS"}'
```

## Database Setup

JPA schema generation is controlled by:

```env
DDL_AUTO=update
```

For production, use a managed MySQL database and keep `DDL_AUTO=update` for simple demos. For stricter production control, replace auto schema updates with a migration tool such as Flyway.

## Railway Deployment

1. Push the repository to GitHub.
2. Create a new Railway project from the GitHub repo.
3. Set the root directory to `backend` if the full repo also contains frontend code.
4. Add a managed MySQL database.
5. Set variables:

```env
DB_URL=jdbc:mysql://YOUR_MYSQL_HOST:3306/YOUR_MYSQL_DATABASE?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=YOUR_MYSQL_USER
DB_PASSWORD=YOUR_MYSQL_PASSWORD
DB_DRIVER=com.mysql.cj.jdbc.Driver
DDL_AUTO=update
JWT_SECRET=replace-with-a-long-production-secret
CORS_ALLOWED_ORIGIN=https://your-angular-frontend-domain.com
SEED_DATA_ENABLED=false
```

6. Deploy. Railway uses `railway.json` and `Procfile` to start the app from the built jar.

## Notes

- Project roles are stored per project in `project_members`.
- There is no global admin role; the project creator becomes `ADMIN` for that project.
- Members can update only the status of tasks assigned to them.
- Admin-only checks are enforced in service methods, not just routing.
