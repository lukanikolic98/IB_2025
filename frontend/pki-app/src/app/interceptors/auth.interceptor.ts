import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    // Get the token from localStorage
    const token = localStorage.getItem('accessToken');

    // Only add the token if it exists and it's a backend API request
    if (token && req.url.startsWith('http://localhost:8080/api')) {
      const authReq = req.clone({
        setHeaders: { Authorization: `Bearer ${token}` },
      });
      return next.handle(authReq);
    }

    // Otherwise, forward the request unmodified
    return next.handle(req);
  }
}
