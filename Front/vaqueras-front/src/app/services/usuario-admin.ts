import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';

export interface UsuarioAdmin {
    idUsuario?: number;
    nickname: string;
    correo: string;
    password?: string;
    fechaNacimiento: string;
    telefono?: string;
    pais?: string;
    idRol?: number;
}

export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data?: T;
}

@Injectable({
    providedIn: 'root'
})
export class UsuarioAdminService {
    private apiUrl = `${backEnd.apiUrl}/usuarios-admin`;

    constructor(private http: HttpClient) { }
    
    listarTodos(): Observable<ApiResponse<UsuarioAdmin[]>> {
        return this.http.get<ApiResponse<UsuarioAdmin[]>>(this.apiUrl);
    }
    
    obtenerPorId(id: number): Observable<ApiResponse<UsuarioAdmin>> {
        return this.http.get<ApiResponse<UsuarioAdmin>>(`${this.apiUrl}/${id}`);
    }
    
    crear(admin: UsuarioAdmin): Observable<ApiResponse<UsuarioAdmin>> {
        return this.http.post<ApiResponse<UsuarioAdmin>>(this.apiUrl, admin);
    }
    
    actualizar(id: number, admin: Partial<UsuarioAdmin>): Observable<ApiResponse<void>> {
        return this.http.put<ApiResponse<void>>(`${this.apiUrl}/${id}`, admin);
    }
    
    cambiarPassword(id: number, password: string): Observable<ApiResponse<void>> {
        return this.http.put<ApiResponse<void>>(`${this.apiUrl}/${id}/password`, { password });
    }

    //Elimina administrador, no se pueden eliminar todos los administradores
    eliminar(id: number): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
    }
}