import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth';

export const authHttpInterceptor: HttpInterceptorFn = (req, next) => {
    const auth = inject(AuthService);
    const router = inject(Router);

    // Solo aplica a requests hacia tu API
    const isApiRequest = req.url.startsWith(auth.apiUrl);

    const reqWithCreds = isApiRequest
        ? req.clone({ withCredentials: true })
        : req;

    return next(reqWithCreds).pipe(
        catchError((err: unknown) => {
            if (err instanceof HttpErrorResponse && err.status === 401) {
                // sesión inválida/expirada → limpiamos user local
                auth.logout

                // evita loops si ya estás en login
                if (router.url !== '/login') {
                    router.navigateByUrl('/login');
                }
            }
            return throwError(() => err);
        })
    );
};