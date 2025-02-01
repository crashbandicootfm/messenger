import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthenticationService } from '../services/authentication.service';
import { RegistrationRequest } from '../models/request/registration-request.model';
import {Router} from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  standalone: true,
  imports: [
    ReactiveFormsModule
  ]
})
export class RegisterComponent {
  registerForm: FormGroup;

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
      const regRequest: RegistrationRequest = this.registerForm.value;
      this.authService.register(regRequest).subscribe({
        next: (response) => {
          console.log('Token:', response.token);
          localStorage.setItem('token', response.token);

          this.router.navigate(['/messenger']);
        },
        error: (err) => {
          console.error('Registration error:', err);
        }
      });
    }
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
