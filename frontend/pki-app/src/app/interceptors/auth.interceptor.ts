// auth.interceptor.fn.ts
import { inject } from '@angular/core';
import {
  HttpRequest,
  HttpHandlerFn,
  HttpEvent,
  HttpErrorResponse,
} from '@angular/common/http';
import {
  Observable,
  BehaviorSubject,
  throwError,
  filter,
  switchMap,
  take,
  catchError,
} from 'rxjs';
import { AuthService } from '../shared/services/auth.service';

const refreshTokenSubject = new BehaviorSubject<string | null>(null);
let isRefreshing = false;

export const authInterceptorFn = (
  req: HttpRequest<any>,
  next: HttpHandlerFn
): Observable<HttpEvent<any>> => {
  const authService = inject(AuthService);

  // Skip /auth/ endpoints
  if (!req.url.includes('/auth/')) {
    const token = authService.getAccessToken();
    if (token) {
      req = req.clone({
        withCredentials: true,
        setHeaders: { Authorization: `Bearer ${token}` },
      });
    }
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !req.url.includes('/auth/refresh')) {
        return handle401Error(req, next, authService);
      }
      return throwError(() => error);
    })
  );
};

function handle401Error(
  req: HttpRequest<any>,
  next: HttpHandlerFn,
  authService: AuthService
): Observable<HttpEvent<any>> {
  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null);

    const refreshToken = authService.getRefreshToken();
    if (!refreshToken) {
      authService.logout();
      return throwError(() => new Error('Refresh token not found'));
    }

    return authService.refreshToken(refreshToken).pipe(
      switchMap((tokens: any) => {
        isRefreshing = false;
        refreshTokenSubject.next(tokens.accessToken);
        return next(
          req.clone({
            withCredentials: true,
            setHeaders: { Authorization: `Bearer ${tokens.accessToken}` },
          })
        );
      }),
      catchError((err) => {
        isRefreshing = false;
        authService.logout();
        return throwError(() => err);
      })
    );
  } else {
    // Wait until refresh is done
    return refreshTokenSubject.pipe(
      filter((token) => token != null),
      take(1),
      switchMap((token) => {
        return next(
          req.clone({
            withCredentials: true,
            setHeaders: { Authorization: `Bearer ${token}` },
          })
        );
      })
    );
  }
}
