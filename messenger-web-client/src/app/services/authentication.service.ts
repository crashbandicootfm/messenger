import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, map, Observable, of, tap, throwError} from 'rxjs';
import { AuthenticationRequest } from '../models/request/authentication-request.model';
import { RegistrationRequest } from '../models/request/registration-request.model';
import { TokenResponse } from '../models/response/token-response.model';
import { UserResponse } from '../models/response/user-response.model';
import nacl from 'tweetnacl';
import util from 'tweetnacl-util';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private readonly apiUrl = "http://localhost:8080/api/v1/authentication";
  private readonly userUrl = "http://localhost:8080/api/v1/users/";
  private twoFactorRequired = false;

  constructor(private http: HttpClient) {}

  isAuthenticated(): boolean {
    const token = sessionStorage.getItem('token');
    return !!token;
  }

  login(authRequest: AuthenticationRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.apiUrl}/login`, authRequest).pipe(
      tap((response: TokenResponse) => {
        sessionStorage.setItem('token', response.token);
      })
    );
  }

  register(regRequest: RegistrationRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.apiUrl}/register`, regRequest);
  }

  refreshToken(): Observable<TokenResponse> {
    const refreshToken = sessionStorage.getItem('refreshToken');
    return this.http.post<TokenResponse>(`${this.apiUrl}/refresh`, { refreshToken });
  }

  getUserProfile(): Observable<{ id: number, username: string, avatarUrl?: string }> {
    const token = sessionStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    return this.http.get<{ id: number, username: string, avatarUrl?: string }>(
      `${this.userUrl}profile`,
      { headers }
    ).pipe(
      tap(profile => {
        if (!profile.avatarUrl) {
          profile.avatarUrl = 'http://localhost:8080/assets/avatar.png';
        }
      })
    );
  }

  getAvatar(userId: number): Observable<Blob> {
    const token = sessionStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    return this.http.get(`${this.userUrl}avatar/${userId}`, {
      headers,
      responseType: 'blob'
    }).pipe(
      tap(blob => {
        if (blob.size === 0) {
          console.warn("Получен пустой аватар!");
        }
      }),
      catchError(err => {
        console.error("Ошибка загрузки аватара", err);
        return of(new Blob());
      })
    );
  }

  searchUsersByName(username: string | null): Observable<UserResponse[]> {
    const token = sessionStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    return this.http.get<UserResponse[]>(`${this.userUrl}search?username=${username}`, { headers });
  }

  generateKeyPair(): { publicKey: string; privateKey: string } {
    const keyPair = nacl.box.keyPair();
    return {
      publicKey: util.encodeBase64(keyPair.publicKey),
      privateKey: util.encodeBase64(keyPair.secretKey),
    };
  }

  getUserById(id: number): Observable<UserResponse> {
    const token = sessionStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    return this.http.get<UserResponse>(`${this.userUrl}/${id}`, { headers });
  }

  getUserByUsername(username: string): Observable<UserResponse> {
    const token = sessionStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    return this.http.get<UserResponse>(`${this.userUrl}username/${username}`, { headers });
  }

  enableTwoFactorAuth(): Observable<string> {
    const token = sessionStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    return this.http.post<{ qrCodeUrl?: string; error?: string }>(
      `${this.userUrl}enable-2fa`, {}, { headers }
    ).pipe(
      map(response => {
        if (response.qrCodeUrl) {
          console.log("QR-код для 2FA:", response.qrCodeUrl);
          return response.qrCodeUrl;
        } else {
          console.error("Ошибка сервера:", response.error);
          return '';
        }
      }),
      catchError(err => {
        console.error("Ошибка при включении 2FA", err);
        return of('');
      })
    );
  }

  verifyTwoFactorCode(code: number): Observable<TokenResponse> {
    const token = sessionStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    return this.http.post<TokenResponse>(
      `${this.userUrl}verify-2fa`,
      { code },
      { headers }
    ).pipe(
      tap(response => {
        sessionStorage.setItem('token', response.token);
        sessionStorage.setItem('refreshToken', response.refreshToken);
      }),
      catchError(err => {
        console.log("Token: " + token);
        console.error("Ошибка верификации 2FA", err);
        return throwError(() => err);
      })
    );
  }

  updateEmail(email: string): Observable<UserResponse> {
    const token = sessionStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    return this.http.post<UserResponse>(
      `${this.userUrl}update-email`,
      { email },
      { headers }
    ).pipe(
      tap(response => {
        console.log("Email успешно обновлён", response);
      }),
      catchError(err => {
        console.error("Ошибка при обновлении email", err);
        return throwError(() => err);
      })
    );
  }
}
