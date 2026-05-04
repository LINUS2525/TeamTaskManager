import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { publicGuard } from './core/guards/public.guard';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { LoginComponent } from './pages/login/login.component';
import { MyTasksComponent } from './pages/my-tasks/my-tasks.component';
import { ProjectDetailComponent } from './pages/project-detail/project-detail.component';
import { ProjectFormComponent } from './pages/project-form/project-form.component';
import { ProjectsListComponent } from './pages/projects-list/projects-list.component';
import { SignupComponent } from './pages/signup/signup.component';
import { TaskFormComponent } from './pages/task-form/task-form.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [publicGuard]
  },
  {
    path: 'signup',
    component: SignupComponent,
    canActivate: [publicGuard]
  },
  {
    path: '',
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'projects', component: ProjectsListComponent },
      { path: 'projects/new', component: ProjectFormComponent },
      { path: 'projects/:projectId', component: ProjectDetailComponent },
      { path: 'projects/:projectId/tasks/new', component: TaskFormComponent },
      { path: 'projects/:projectId/tasks/:taskId/edit', component: TaskFormComponent },
      { path: 'my-tasks', component: MyTasksComponent }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
