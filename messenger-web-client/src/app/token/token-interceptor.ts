import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import {Observable, of, throwError} from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { Router } from '@angular/router';
import {AuthenticationService} from '../services/authentication.service';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  constructor(private authService: AuthenticationService, private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Get the token from local storage
    const token = localStorage.getItem('token');

    console.log('Intercepting request. Current token:', token);  // Log current token

    // If no token, proceed with the request
    if (!token) {
      console.log('No token found. Proceeding with request...');
      return next.handle(req);
    }

    // Clone the request and set the authorization header
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    console.log('Proceeding with request. Authorization header set.');

    // Proceed with the request
    return next.handle(clonedRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        // If the token is expired (401 Unauthorized)
        if (error.status === 401) {
          console.log('Token expired (401). Attempting to refresh token...');

          // Try to refresh the token
          return this.authService.refreshToken().pipe(
            switchMap((newTokenResponse) => {
              console.log('Token refreshed successfully! New token:', newTokenResponse.token);

              // Save the new token
              localStorage.setItem('token', newTokenResponse.token);
              localStorage.setItem('refreshToken', newTokenResponse.refreshToken);

              // Clone the request with the new token
              const clonedRequestWithNewToken = req.clone({
                setHeaders: {
                  Authorization: `Bearer ${newTokenResponse.token}`
                }
              });

              console.log('Retrying original request with new token...');

              // Retry the original request with the new token
              return next.handle(clonedRequestWithNewToken);
            })
          );
        }

        // If it's any other error, propagate the error (throwError instead of of(error))
        console.log('Error occurred (not token expiration). Propagating error...');
        return throwError(error);
      })
    );
  }
}
