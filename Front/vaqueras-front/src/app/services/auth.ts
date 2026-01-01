import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { backEnd } from '../app.config';
import { Usuario, LoginRequest, RegistroRequest } from '../models/usuario';
import { UsuarioEmpresa } from '../models/empresa';
import { ApiResponse,Rol } from '../models/enums';



@Injectable({
  providedIn: 'root',
})

export class AuthService {
  private apiUrl = backEnd.apiUrl
  private currentUserSubject = new BehaviorSubject<Usuario | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    const usuarioGuardado = localStorage.getItem('usuario');
    if (usuarioGuardado) {
      try {
        this.currentUserSubject.next(JSON.parse(usuarioGuardado));
      } catch (error) {
        console.error('Error al parsear usuario guardado:', error);
        this.logout();
      }
      
    }  
    console.log('AuthService inicializado');
    console.log('Backend URL:', this.apiUrl);
  } 

login(correo: string, password: string): Observable<ApiResponse<Usuario>> {
    return this.http.post<ApiResponse<Usuario>>(`${this.apiUrl}/login`, { correo, password })
      .pipe(
        tap(response => {
          if (response.success && response.data) {
            // Guardar en localStorage
            localStorage.setItem('usuario', JSON.stringify(response.data));
            this.currentUserSubject.next(response.data);

            //redirigir al dashboard correspondiente
            this.redirectToDashboard(response.data.idRol)
          }
        })
      );
  }

  registro(datos: RegistroRequest): Observable<ApiResponse<Usuario>> {
    return this.http.post<ApiResponse<Usuario>>(`${this.apiUrl}/registro`, datos);
  }

  //Cierra sesion
  logout(): void {
    localStorage.removeItem('usuario');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login'])
  }

  isAuthenticated(): boolean {
    return this.currentUserSubject.value !== null;
  }

  getCurrentUser(): Usuario | null {
    return this.currentUserSubject.value;
  }
    
  hasRole(rol: Rol): boolean {
    const usuario = this.getCurrentUser();
    return usuario !== null && usuario.idRol === rol;
  }
  
  hasAnyRole(roles: Rol[]): boolean {
    const usuario = this.getCurrentUser();
    return usuario !== null && roles.includes(usuario.idRol);
  }

  //redirige al dashboard segun el rol
  redirectToDashboard(idRol: Rol): void {
    switch (idRol) {
      case Rol.ADMINISTRADOR:
        this.router.navigate(['/admin/dashboard']);
        break;
      case Rol.DUENO_EMPRESA:
        this.router.navigate(['/empresa/dashboard']);
        break;
      case Rol.JUGADOR:
        this.router.navigate(['/jugador/dashboard']);
        break;
      default:
        this.router.navigate(['/login']);
    }
  }

  //obtiene rol
  getRoleName(idRol: Rol): string {
    switch (idRol) {
      case Rol.ADMINISTRADOR:
        return 'Administrador';
      case Rol.DUENO_EMPRESA:
        return 'Dueño de Empresa';
      case Rol.JUGADOR:
        return 'Jugador';
      default:
        return 'Usuario';
    }
  }
}

