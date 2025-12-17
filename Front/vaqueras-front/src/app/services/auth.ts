import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface LoginRequest {
  correo: string;
  password: string;
}

export interface RegistroRequest {
  nickname: string;
  correo: string;
  password: string;
  fechaNacimiento: string;
  telefono?: string;
  pais: string;
  idRol: number;
}

export interface Usuario {
  idUsuario: number;
  nickname: string;
  correo: string;
  pais: string;
  idRol: number;
}

export interface LoginResponse {
  success: boolean;
  message: string;
  data: Usuario | null;
}

export interface RegistroResponse {
  success: boolean;
  message: string;
  data: any;
}

@Injectable({
  providedIn: 'root',
})

export class AuthService {
  private apiUrl = 'http://localhost:8080/vaqueras_IPC2';
  private currentUserSubject = new BehaviorSubject<Usuario | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Cargar usuario desde localStorage al iniciar
    const usuarioGuardado = localStorage.getItem('usuario');
    if (usuarioGuardado) {
      this.currentUserSubject.next(JSON.parse(usuarioGuardado));
    }  
  }

login(correo: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, { correo, password })
      .pipe(
        tap(response => {
          if (response.success && response.data) {
            // Guardar en localStorage
            localStorage.setItem('usuario', JSON.stringify(response.data));
            this.currentUserSubject.next(response.data);
          }
        })
      );
  }

  registro(datos: RegistroRequest): Observable<RegistroResponse> {
    return this.http.post<RegistroResponse>(`${this.apiUrl}/registro`, datos);
  }
  
  logout(): void {
    localStorage.removeItem('usuario');
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    return this.currentUserSubject.value !== null;
  }

  getCurrentUser(): Usuario | null {
    return this.currentUserSubject.value;
  }
  
}

