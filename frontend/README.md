# Team Task Manager Frontend

Angular 17 frontend for the Team Task Manager Spring Boot backend.

## Features

- Login and signup screens
- JWT auth handling with route guard and HTTP interceptor
- Dashboard with task counts and project progress
- Projects list and create project page
- Project detail page with members and tasks
- Admin member management
- Admin task create, edit, and delete flows
- My Tasks page with assignee status updates
- Responsive, professional UI

## Tech Stack

- Angular 17
- TypeScript
- Angular Router
- Reactive Forms
- HttpClient
- Standalone components

## Local Setup

Start the backend first:

```bash
cd C:\Users\sunil\Desktop\Java-Projects\TeamTaskManager\backend
mvn spring-boot:run
```

Then start the frontend:

```bash
cd C:\Users\sunil\Desktop\Java-Projects\TeamTaskManager\frontend
npm install
npm start
```

Open:

```text
http://localhost:4200
```

The frontend calls:

```text
http://localhost:8080/api
```

## Demo Credentials

```text
Admin:
admin@teamtaskmanager.com
Admin@12345

Member:
member@teamtaskmanager.com
Member@12345
```

## Environment

Local API config:

```ts
src/environments/environment.ts
```

Production API config:

```ts
src/environments/environment.prod.ts
```

Update `environment.prod.ts` before deploying the Angular app:

```ts
export const environment = {
  production: true,
  apiUrl: 'https://your-railway-backend-url.up.railway.app/api'
};
```

## Build

```bash
npm run build
```

Build output:

```text
dist/team-task-manager-frontend
```

## Deployment

You can deploy the Angular app to Vercel, Netlify, Railway static hosting, or any static host.

Recommended build settings:

```text
Root directory: frontend
Build command: npm install && npm run build
Publish directory: dist/team-task-manager-frontend/browser
```

Set the backend `CORS_ALLOWED_ORIGIN` environment variable to your deployed frontend URL.
