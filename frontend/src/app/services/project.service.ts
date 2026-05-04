import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  AddProjectMemberRequest,
  CreateProjectRequest,
  Project,
  ProjectDetail,
  ProjectMember
} from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/projects`);
  }

  getProject(projectId: number): Observable<ProjectDetail> {
    return this.http.get<ProjectDetail>(`${this.apiUrl}/projects/${projectId}`);
  }

  createProject(request: CreateProjectRequest): Observable<ProjectDetail> {
    return this.http.post<ProjectDetail>(`${this.apiUrl}/projects`, request);
  }

  addMember(projectId: number, request: AddProjectMemberRequest): Observable<ProjectMember> {
    return this.http.post<ProjectMember>(`${this.apiUrl}/projects/${projectId}/members`, request);
  }

  removeMember(projectId: number, userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/projects/${projectId}/members/${userId}`);
  }

  deleteProject(projectId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/projects/${projectId}`);
  }
}
