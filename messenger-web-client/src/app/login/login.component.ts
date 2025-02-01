import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { AuthenticationService } from '../services/authentication.service';
import { AuthenticationRequest } from '../models/request/authentication-request.model';
import {NgIf} from '@angular/common';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  imports: [
    ReactiveFormsModule,
  ],
  standalone: true,
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthenticationService,
    private router: Router
  ) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const authRequest: AuthenticationRequest = this.loginForm.value;
      this.authService.login(authRequest).subscribe({
        next: (response) => {
          console.log('Token:', response.token);
          localStorage.setItem('token', response.token);

          this.router.navigate(['/messenger']);
        },
        error: (err) => {
          console.error('Login error:', err);
        }
      });
    } else {
      console.log("Form is invalid");
    }
  }

  goToRegister() {
    this.router.navigate(['/register'])
  }
}
