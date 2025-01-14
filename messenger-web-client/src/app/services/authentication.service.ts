import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import { AuthenticationRequest } from '../models/request/authentication-request.model';
import { RegistrationRequest } from '../models/request/registration-request.model';
import { TokenResponse } from '../models/response/token-response.model';
import { UserResponse } from '../models/response/user-response.model';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private readonly apiUrl = "http://localhost:8080/api/v1/authentication";
  private readonly userUrl = "http://localhost:8080/api/v1/users/";

  constructor(private http: HttpClient) {}

  isAuthenticated(): boolean {
    const token = localStorage.getItem('token');
    return !!token;
  }

  login(authRequest: AuthenticationRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.apiUrl}/login`, authRequest).pipe(
      tap((response: TokenResponse) => {
        localStorage.setItem('token', response.token);
      })
    );
  }

  register(regRequest: RegistrationRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.apiUrl}/register`, regRequest);
  }

  getUserProfile(): Observable<{ id: number, username: string, avatarUrl: string }> {
    const token = localStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    return this.http.get<{ id: number, username: string, avatarUrl: string }>(`${this.userUrl}profile`, { headers });
  }

  getAvatar(userId: number): Observable<Blob> {
    const token = localStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    return this.http.get<Blob>(`${this.userUrl}avatar/${userId}`, { headers, responseType: 'blob' as 'json' });
  }


  getUserById(id: number): Observable<UserResponse> {
    const token = localStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    return this.http.get<UserResponse>(`${this.userUrl}/${id}`, { headers });
  }

  getUserByUsername(username: string): Observable<UserResponse> {
    const token = localStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    return this.http.get<UserResponse>(`${this.userUrl}/username/${username}`, { headers });
  }
}
