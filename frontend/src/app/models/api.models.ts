export type ProjectRole = 'ADMIN' | 'MEMBER';
export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE';
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH';

export interface UserSummary {
  id: number;
  name: string;
  email: string;
}

export interface AuthResponse {
  token: string;
  tokenType: 'Bearer';
  expiresInMs: number;
  user: UserSummary;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface SignupRequest {
  name: string;
  email: string;
  password: string;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors?: Record<string, string>;
}

export interface ProjectMember {
  id: number;
  user: UserSummary;
  role: ProjectRole;
  joinedAt: string;
}

export interface Project {
  id: number;
  name: string;
  description?: string | null;
  myRole: ProjectRole;
  memberCount: number;
  taskCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface ProjectDetail {
  id: number;
  name: string;
  description?: string | null;
  myRole: ProjectRole;
  members: ProjectMember[];
  tasks: Task[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateProjectRequest {
  name: string;
  description?: string | null;
}

export interface AddProjectMemberRequest {
  email: string;
  role: ProjectRole;
}

export interface Task {
  id: number;
  title: string;
  description?: string | null;
  status: TaskStatus;
  priority: TaskPriority;
  dueDate?: string | null;
  assignee?: UserSummary | null;
  projectId: number;
  projectName: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTaskRequest {
  title: string;
  description?: string | null;
  status?: TaskStatus;
  priority?: TaskPriority;
  dueDate?: string | null;
  assigneeId?: number | null;
}

export interface UpdateTaskRequest {
  title?: string;
  description?: string | null;
  status?: TaskStatus;
  priority?: TaskPriority;
  dueDate?: string | null;
  assigneeId?: number | null;
}

export interface UpdateTaskStatusRequest {
  status: TaskStatus;
}

export interface ProjectProgress {
  projectId: number;
  projectName: string;
  totalTasks: number;
  todoCount: number;
  inProgressCount: number;
  doneCount: number;
  overdueCount: number;
  completionPercent: number;
}

export interface Dashboard {
  totalTasks: number;
  todoCount: number;
  inProgressCount: number;
  doneCount: number;
  overdueCount: number;
  myAssignedTasks: Task[];
  projectProgress: ProjectProgress[];
}
