import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

export interface AuthUser {
  id: number;
  nombreYApellido: string;
  email: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = environment.apiUrl;

  /** Current user, null if not logged in */
  private _user = signal<AuthUser | null>(null);
  private _checked = signal(false);

  /** Public read-only signals */
  readonly user = this._user.asReadonly();
  readonly isLoggedIn = computed(() => this._user() !== null);
  readonly checked = this._checked.asReadonly();

  constructor(private http: HttpClient, private router: Router) {}

  /** Call once at app startup (or when needed) to check session */
  checkSession(): void {
    this.http.get<AuthUser>(`${this.baseUrl}/usuario/me`).subscribe({
      next: (user) => {
        this._user.set(user);
        this._checked.set(true);
      },
      error: () => {
        this._user.set(null);
        this._checked.set(true);
      },
    });
  }

  /** Mark user as logged in (call after successful login) */
  setLoggedIn(user?: AuthUser): void {
    if (user) {
      this._user.set(user);
    } else {
      // Re-check session from server
      this.checkSession();
    }
  }

  logout(): void {
    this.http.post(`${this.baseUrl}/usuario/logout`, {}).subscribe({
      next: () => {
        this._user.set(null);
        this.router.navigate(['/login']);
      },
      error: () => {
        this._user.set(null);
        this.router.navigate(['/login']);
      },
    });
  }
}
