import { HttpClient } from '@angular/common/http';
import { Injectable, computed, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, SignupRequest, UserSummary } from '../../models/api.models';

const TOKEN_KEY = 'team-task-manager-token';
const USER_KEY = 'team-task-manager-user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = environment.apiUrl;
  private readonly userSignal = signal<UserSummary | null>(this.readStoredUser());
  private readonly tokenSignal = signal<string | null>(this.readStoredToken());

  readonly currentUser = computed(() => this.userSignal());
  readonly isAuthenticated = computed(() => Boolean(this.tokenSignal()));

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {}

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, request).pipe(
      tap((response) => this.persistSession(response))
    );
  }

  signup(request: SignupRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/signup`, request).pipe(
      tap((response) => this.persistSession(response))
    );
  }

  refreshCurrentUser(): Observable<UserSummary> {
    return this.http.get<UserSummary>(`${this.apiUrl}/users/me`).pipe(
      tap((user) => {
        localStorage.setItem(USER_KEY, JSON.stringify(user));
        this.userSignal.set(user);
      })
    );
  }

  getToken(): string | null {
    return this.tokenSignal();
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.tokenSignal.set(null);
    this.userSignal.set(null);
  }

  logoutAndRedirect(): void {
    this.logout();
    this.router.navigate(['/login']);
  }

  private persistSession(response: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, response.token);
    localStorage.setItem(USER_KEY, JSON.stringify(response.user));
    this.tokenSignal.set(response.token);
    this.userSignal.set(response.user);
  }

  private readStoredToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  private readStoredUser(): UserSummary | null {
    const rawUser = localStorage.getItem(USER_KEY);
    if (!rawUser) {
      return null;
    }

    try {
      return JSON.parse(rawUser) as UserSummary;
    } catch {
      localStorage.removeItem(USER_KEY);
      return null;
    }
  }
}
