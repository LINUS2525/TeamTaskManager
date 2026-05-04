import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin, finalize } from 'rxjs';
import { ProjectDetail, Task, TaskPriority, TaskStatus } from '../../models/api.models';
import { extractApiError } from '../../services/api-error.util';
import { ProjectService } from '../../services/project.service';
import { TaskService } from '../../services/task.service';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './task-form.component.html',
  styleUrl: './task-form.component.css'
})
export class TaskFormComponent implements OnInit {
  projectId = 0;
  taskId: number | null = null;
  project: ProjectDetail | null = null;
  task: Task | null = null;
  loading = true;
  saving = false;
  error = '';

  readonly form = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.maxLength(180)]],
    description: ['', [Validators.maxLength(4000)]],
    status: ['TODO' as TaskStatus, [Validators.required]],
    priority: ['MEDIUM' as TaskPriority, [Validators.required]],
    dueDate: [''],
    assigneeId: ['']
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
    const rawTaskId = this.route.snapshot.paramMap.get('taskId');
    this.taskId = rawTaskId ? Number(rawTaskId) : null;
    this.loadData();
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const payload = {
      title: raw.title,
      description: raw.description || null,
      status: raw.status,
      priority: raw.priority,
      dueDate: raw.dueDate || null,
      assigneeId: raw.assigneeId ? Number(raw.assigneeId) : null
    };

    this.saving = true;
    this.error = '';

    const request$ = this.taskId
      ? this.taskService.updateTask(this.taskId, payload)
      : this.taskService.createTask(this.projectId, payload);

    request$
      .pipe(finalize(() => (this.saving = false)))
      .subscribe({
        next: () => this.router.navigate(['/projects', this.projectId]),
        error: (error) => (this.error = extractApiError(error))
      });
  }

  private loadData(): void {
    this.loading = true;
    this.error = '';

    if (this.taskId) {
      forkJoin({
        project: this.projectService.getProject(this.projectId),
        task: this.taskService.getTask(this.taskId)
      })
        .pipe(finalize(() => (this.loading = false)))
        .subscribe({
          next: ({ project, task }) => {
            this.project = project;
            this.task = task;
            this.form.patchValue({
              title: task.title,
              description: task.description || '',
              status: task.status,
              priority: task.priority,
              dueDate: task.dueDate || '',
              assigneeId: task.assignee?.id ? String(task.assignee.id) : ''
            });
          },
          error: (error) => (this.error = extractApiError(error))
        });
      return;
    }

    this.projectService.getProject(this.projectId)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (project) => (this.project = project),
        error: (error) => (this.error = extractApiError(error))
      });
  }
}
