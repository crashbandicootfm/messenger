import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { AuthenticationService } from '../services/authentication.service';
import { AuthenticationRequest } from '../models/request/authentication-request.model';
import {NgIf} from '@angular/common';
import {Router} from '@angular/router';
import {TokenResponse} from '../models/response/token-response.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  imports: [
    ReactiveFormsModule,
    NgIf,
  ],
  standalone: true,
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  twoFactorForm: FormGroup;
  twoFactorRequired: boolean = false;
  storedUsername: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthenticationService,
    private router: Router
  ) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    this.twoFactorForm = this.formBuilder.group({
      twoFactorCode: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const authRequest: AuthenticationRequest = this.loginForm.value;
      this.authService.login(authRequest).subscribe({
        next: (response) => {
          if (response.twoFactorRequired) {
            this.twoFactorRequired = true;
            this.storedUsername = authRequest.username;
          } else {
            this.storeTokensAndRedirect(response);
          }
        },
        error: (err) => {
          console.error('Login error:', err);
        }
      });
    }
  }

  onSubmitTwoFactor() {
    if (this.twoFactorForm.valid && this.storedUsername) {
      const twoFactorCode = parseInt(this.twoFactorForm.value.twoFactorCode, 10);
      if (isNaN(twoFactorCode)) {
        console.error('Invalid twoFactorCode: not a number');
        return;
      }

      console.log('Sending twoFactorCode:', twoFactorCode);

      this.authService.verifyTwoFactorCode(twoFactorCode).subscribe({
        next: (response) => this.storeTokensAndRedirect(response),
        error: (err) => console.error('2FA verification failed', err)
      });
    }
  }

  storeTokensAndRedirect(response: TokenResponse) {
    localStorage.setItem('token', response.token);
    localStorage.setItem('refreshToken', response.refreshToken);
    this.router.navigate(['/messenger']);
  }

  goToRegister() {
    this.router.navigate(['/register']);
  }
}

