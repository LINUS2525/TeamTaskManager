import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { Project } from '../../models/api.models';
import { extractApiError } from '../../services/api-error.util';
import { ProjectService } from '../../services/project.service';

@Component({
  selector: 'app-projects-list',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './projects-list.component.html',
  styleUrl: './projects-list.component.css'
})
export class ProjectsListComponent implements OnInit {
  projects: Project[] = [];
  loading = true;
  error = '';

  constructor(private readonly projectService: ProjectService) {}

  ngOnInit(): void {
    this.projectService.getProjects()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (projects) => (this.projects = projects),
        error: (error) => (this.error = extractApiError(error))
      });
  }
}
