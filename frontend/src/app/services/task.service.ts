import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  CreateTaskRequest,
  Task,
  UpdateTaskRequest,
  UpdateTaskStatusRequest
} from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  createTask(projectId: number, request: CreateTaskRequest): Observable<Task> {
    return this.http.post<Task>(`${this.apiUrl}/projects/${projectId}/tasks`, request);
  }

  getProjectTasks(projectId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/projects/${projectId}/tasks`);
  }

  getMyTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/tasks/my`);
  }

  getTask(taskId: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/tasks/${taskId}`);
  }

  updateTask(taskId: number, request: UpdateTaskRequest): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/tasks/${taskId}`, request);
  }

  updateTaskStatus(taskId: number, request: UpdateTaskStatusRequest): Observable<Task> {
    return this.http.patch<Task>(`${this.apiUrl}/tasks/${taskId}/status`, request);
  }

  deleteTask(taskId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/tasks/${taskId}`);
  }
}
