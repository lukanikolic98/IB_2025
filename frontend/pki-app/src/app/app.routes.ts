import { Routes } from '@angular/router';
import { HeroPageComponent } from './pages/hero-page/hero-page.component';
import { LoginPageComponent } from './pages/auth/login-page/login-page.component';
import { RegisterPageComponent } from './pages/auth/register-page/register-page.component';
import { ProfilePageComponent } from './pages/profile-page/profile-page.component';
import { AdminDashboardPageComponent } from './pages/admin-dashboard-page/admin-dashboard-page.component';
import { CreateCertificatePageComponent } from './pages/create-certificate-page/create-certificate-page.component';

export const routes: Routes = [
  { path: '', component: HeroPageComponent }, // hero page
  { path: 'login', component: LoginPageComponent }, // login page
  { path: 'register', component: RegisterPageComponent }, // register page
  { path: 'admin/certificates', component: AdminDashboardPageComponent }, // updated dashboard route
  {
    path: 'admin/certificates/create',
    component: CreateCertificatePageComponent,
  }, // new create certificate page
  { path: 'profile', component: ProfilePageComponent },
  { path: '**', redirectTo: '', pathMatch: 'full' }, // fallback -> hero
];
