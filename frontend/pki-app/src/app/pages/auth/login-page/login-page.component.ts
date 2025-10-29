import { Component, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../shared/services/auth.service';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ButtonComponent, RouterModule],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.scss',
})
export class LoginPageComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  // Define the form group
  applyForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  // Login method
  submitLogin() {
    if (this.applyForm.invalid) {
      this.applyForm.markAllAsTouched();
      return;
    }

    const { email, password } = this.applyForm.getRawValue();

    console.log('Attempting login with', email);

    this.authService
      .submitLogin({
        email: email ?? '',
        password: password ?? '',
      })
      .subscribe({
        next: (response: any) => {
          console.log('Login successful:', response);
          alert(`Welcome ${response.firstname}!`);
          this.router.navigate(['/admin/certificates']);
        },
        error: (err) => {
          console.error('Login failed:', err);
          alert('Invalid credentials. Please try again.');
        },
      });
  }
}
