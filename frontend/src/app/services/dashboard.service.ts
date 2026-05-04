import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Dashboard, ProjectProgress } from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  getDashboard(): Observable<Dashboard> {
    return this.http.get<Dashboard>(`${this.apiUrl}/dashboard`);
  }

  getProjectDashboard(projectId: number): Observable<ProjectProgress> {
    return this.http.get<ProjectProgress>(`${this.apiUrl}/projects/${projectId}/dashboard`);
  }
}
