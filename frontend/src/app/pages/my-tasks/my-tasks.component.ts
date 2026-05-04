import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { Task, TaskStatus } from '../../models/api.models';
import { extractApiError } from '../../services/api-error.util';
import { TaskService } from '../../services/task.service';

@Component({
  selector: 'app-my-tasks',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './my-tasks.component.html',
  styleUrl: './my-tasks.component.css'
})
export class MyTasksComponent implements OnInit {
  tasks: Task[] = [];
  loading = true;
  updatingTaskId: number | null = null;
  error = '';

  constructor(private readonly taskService: TaskService) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  updateStatus(task: Task, status: string): void {
    this.updatingTaskId = task.id;
    this.error = '';

    this.taskService.updateTaskStatus(task.id, { status: status as TaskStatus })
      .pipe(finalize(() => (this.updatingTaskId = null)))
      .subscribe({
        next: (updatedTask) => {
          this.tasks = this.tasks.map((item) => item.id === updatedTask.id ? updatedTask : item);
        },
        error: (error) => (this.error = extractApiError(error))
      });
  }

  statusClass(task: Task): string {
    if (task.status === 'DONE') {
      return 'done';
    }
    return task.status === 'IN_PROGRESS' ? 'progress' : 'todo';
  }

  priorityClass(task: Task): string {
    return task.priority === 'HIGH' ? 'high' : '';
  }

  private loadTasks(): void {
    this.loading = true;
    this.error = '';

    this.taskService.getMyTasks()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (tasks) => (this.tasks = tasks),
        error: (error) => (this.error = extractApiError(error))
      });
  }
}
