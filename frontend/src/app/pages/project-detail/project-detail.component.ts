import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { ProjectDetail, ProjectRole, Task } from '../../models/api.models';
import { extractApiError } from '../../services/api-error.util';
import { ProjectService } from '../../services/project.service';
import { TaskService } from '../../services/task.service';

@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './project-detail.component.html',
  styleUrl: './project-detail.component.css'
})
export class ProjectDetailComponent implements OnInit {
  project: ProjectDetail | null = null;
  projectId = 0;
  loading = true;
  memberLoading = false;
  actionLoading = false;
  error = '';
  memberError = '';

  readonly memberForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    role: ['MEMBER' as ProjectRole, [Validators.required]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly projectService: ProjectService,
    private readonly taskService: TaskService
  ) {}

  ngOnInit(): void {
    this.projectId = Number(this.route.snapshot.paramMap.get('projectId'));
    this.loadProject();
  }

  loadProject(): void {
    this.loading = true;
    this.error = '';

    this.projectService.getProject(this.projectId)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (project) => (this.project = project),
        error: (error) => (this.error = extractApiError(error))
      });
  }

  addMember(): void {
    if (this.memberForm.invalid) {
      this.memberForm.markAllAsTouched();
      return;
    }

    this.memberLoading = true;
    this.memberError = '';

    this.projectService.addMember(this.projectId, this.memberForm.getRawValue())
      .pipe(finalize(() => (this.memberLoading = false)))
      .subscribe({
        next: () => {
          this.memberForm.reset({ email: '', role: 'MEMBER' });
          this.loadProject();
        },
        error: (error) => (this.memberError = extractApiError(error))
      });
  }

  removeMember(userId: number): void {
    if (!confirm('Remove this member from the project?')) {
      return;
    }

    this.actionLoading = true;
    this.projectService.removeMember(this.projectId, userId)
      .pipe(finalize(() => (this.actionLoading = false)))
      .subscribe({
        next: () => this.loadProject(),
        error: (error) => (this.error = extractApiError(error))
      });
  }

  deleteTask(taskId: number): void {
    if (!confirm('Delete this task?')) {
      return;
    }

    this.actionLoading = true;
    this.taskService.deleteTask(taskId)
      .pipe(finalize(() => (this.actionLoading = false)))
      .subscribe({
        next: () => this.loadProject(),
        error: (error) => (this.error = extractApiError(error))
      });
  }

  deleteProject(): void {
    if (!confirm('Delete this project and all tasks?')) {
      return;
    }

    this.actionLoading = true;
    this.projectService.deleteProject(this.projectId)
      .pipe(finalize(() => (this.actionLoading = false)))
      .subscribe({
        next: () => this.router.navigate(['/projects']),
        error: (error) => (this.error = extractApiError(error))
      });
  }

  isAdmin(): boolean {
    return this.project?.myRole === 'ADMIN';
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
}
