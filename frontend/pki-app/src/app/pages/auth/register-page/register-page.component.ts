import { Component, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../shared/services/auth.service';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ButtonComponent, RouterModule],
  templateUrl: './register-page.component.html',
  styleUrl: './register-page.component.scss',
})
export class RegisterPageComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  passwordStrength: 'weak' | 'medium' | 'strong' = 'weak';
  passwordStrengthMessage: string = '';

  // Reactive form
  registerForm = this.fb.group({
    firstname: ['', [Validators.required]],
    lastname: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required]],
  });

  // Check password strength
  checkPasswordStrength(value: string) {
    const regexStrong = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/;
    const regexMedium = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,}$/;

    if (regexStrong.test(value)) {
      this.passwordStrength = 'strong';
      this.passwordStrengthMessage = 'Strong password';
    } else if (regexMedium.test(value)) {
      this.passwordStrength = 'medium';
      this.passwordStrengthMessage = 'Medium strength password';
    } else {
      this.passwordStrength = 'weak';
      this.passwordStrengthMessage = 'Weak password';
    }
  }

  // Submit registration
  submitRegister() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const { firstname, lastname, email, password, confirmPassword } =
      this.registerForm.getRawValue();

    if (password !== confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    if (this.passwordStrength === 'weak') {
      alert('Password is too weak. Please use a stronger password.');
      return;
    }

    // Ensure all values are strings (no null)
    this.authService
      .submitRegister({
        firstname: firstname ?? '',
        lastname: lastname ?? '',
        email: email ?? '',
        password: password ?? '',
      })
      .subscribe({
        next: (response: any) => {
          console.log('Registration successful:', response);
          alert(`Welcome ${response.firstname}!`);
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.error('Registration failed:', err);
        },
      });
  }
}
