import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { Dashboard, Task } from '../../models/api.models';
import { DashboardService } from '../../services/dashboard.service';
import { extractApiError } from '../../services/api-error.util';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  dashboard: Dashboard | null = null;
  loading = true;
  error = '';

  constructor(private readonly dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.dashboardService.getDashboard()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (dashboard) => (this.dashboard = dashboard),
        error: (error) => (this.error = extractApiError(error))
      });
  }

  statusClass(task: Task): string {
    if (task.status === 'DONE') {
      return 'done';
    }
    return task.status === 'IN_PROGRESS' ? 'progress' : 'todo';
  }
}
