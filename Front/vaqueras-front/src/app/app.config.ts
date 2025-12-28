import { ApplicationConfig, provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authHttpInterceptor } from './http/auth-http.interceptor';


export const backEnd = {
  production: false,
  apiUrl: 'http://localhost:8080/vaqueras_IPC2-1.0-SNAPSHOT'
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([authHttpInterceptor])
    )
  ]
};