import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthenticationService } from '../services/authentication.service';
import { RegistrationRequest } from '../models/request/registration-request.model';
import {Router} from '@angular/router';
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  standalone: true,
    imports: [
        ReactiveFormsModule,
        NgIf
    ]
})
export class RegisterComponent {
  registerForm: FormGroup;
  registrationError: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthenticationService,
    private router: Router
  ) {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit() {
    if (this.registerForm.valid) {
      this.registrationError = '';

      const regRequest: RegistrationRequest = this.registerForm.value;
      this.authService.register(regRequest).subscribe({
        next: (response) => {
          console.log('Token:', response.token);
          sessionStorage.setItem('token', response.token);
          this.router.navigate(['/messenger']);
        },
        error: (err) => {
          console.error('Registration error:', err);

          if (err.status === 400 && err.error?.message?.includes('already registered')) {
            this.registrationError = 'This username is already taken.';
          } else {
            this.registrationError = 'Username is already exist';
          }
        }
      });
    }
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
