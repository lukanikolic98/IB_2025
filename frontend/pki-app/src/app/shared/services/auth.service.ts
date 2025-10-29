import { Injectable } from '@angular/core';
import {
  UserRegisterDto,
  UserLoginDto,
  LoginResponseDto,
} from '../../types/auth';
import { HttpClient } from '@angular/common/http';
import { Observable, of as observableOf, BehaviorSubject, tap } from 'rxjs';
import { Router } from '@angular/router';
import { jwtDecode, JwtPayload } from 'jwt-decode';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly API_URL = 'https://localhost:8080/auth';
  public isLoggedIn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(
    false
  );
  constructor(private httpClient: HttpClient, private router: Router) {
    const token = localStorage.getItem('accessToken');
    this.isLoggedIn.next(!!token);
  }

  confirmAccount(token: String) {
    return this.httpClient.get(`${this.API_URL}/confirm-registration/${token}`);
  }

  submitRegister(user: UserRegisterDto) {
    console.log('Sending registration request for', user.email);
    return this.httpClient.post(`${this.API_URL}/register`, user);

    //this.router.navigate(['/confirm-email']);
    // router navigation should be handled in the component
  }

  submitLogin(user: UserLoginDto) {
    return this.httpClient
      .post<LoginResponseDto>(`${this.API_URL}/login`, user)
      .pipe(
        tap((response) => {
          // Save tokens & user info
          localStorage.setItem('accessToken', response.accessToken);
          localStorage.setItem('refreshToken', response.refreshToken);
          localStorage.setItem(
            'user',
            JSON.stringify({
              email: response.email,
              firstname: response.firstname,
              lastname: response.lastname,
            })
          );

          this.isLoggedIn.next(true);
        })
      );
  }
  logout() {
    // Clear tokens
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    this.isLoggedIn.next(false);
    this.router.navigate(['/login']);
  }
  getUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }
  getAccessToken() {
    return localStorage.getItem('accessToken');
  }

  getRefreshToken() {
    return localStorage.getItem('refreshToken');
  }
  refreshToken(refreshToken: string) {
    return this.httpClient
      .post<any>(`${this.API_URL}/refresh`, { refreshToken })
      .pipe(
        tap((tokens) => {
          localStorage.setItem('accessToken', tokens.accessToken);
        })
      );
  }
  private isTokenValid(token: string): boolean {
    try {
      const decoded = jwtDecode<JwtPayload>(token);
      const now = Date.now() / 1000;
      return decoded.exp !== undefined && decoded.exp > now;
    } catch (e) {
      return false;
    }
  }
}
