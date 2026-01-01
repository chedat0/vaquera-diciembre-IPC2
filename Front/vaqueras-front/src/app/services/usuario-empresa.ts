import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';

export interface UsuarioEmpresa {
    idUsuario?: number;
    correo: string;
    nombre: string;
    fechaNacimiento: string; // YYYY-MM-DD
    password?: string;
    idEmpresa: number;
}

export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data?: T;
}

@Injectable({
    providedIn: 'root'
})
export class UsuarioEmpresaService {
    private apiUrl = `${backEnd.apiUrl}/usuarios-empresa`;

    constructor(private http: HttpClient) { }

    /**
     * Listar usuarios de una empresa
     *  /usuarios-empresa/empresa/{idEmpresa}
     */
    listarPorEmpresa(idEmpresa: number): Observable<ApiResponse<UsuarioEmpresa[]>> {
        return this.http.get<ApiResponse<UsuarioEmpresa[]>>(`${this.apiUrl}/empresa/${idEmpresa}`);
    }

    /**
     * Obtener usuario por ID
     * /usuarios-empresa/{id}
     */
    obtenerPorId(idUsuario: number): Observable<ApiResponse<UsuarioEmpresa>> {
        return this.http.get<ApiResponse<UsuarioEmpresa>>(`${this.apiUrl}/${idUsuario}`);
    }

    /**
     * Crear usuario de empresa
     * Endpoint: POST /usuarios-empresa
     * 
     * VALIDACIONES BACKEND:
     * - Correo obligatorio y único
     * - Nombre obligatorio
     * - Fecha de nacimiento obligatoria
     * - Password obligatorio
     * - ID de empresa obligatorio
     */
    crear(usuario: UsuarioEmpresa): Observable<ApiResponse<UsuarioEmpresa>> {
        return this.http.post<ApiResponse<UsuarioEmpresa>>(this.apiUrl, usuario);
    }

    /**
     * Actualizar datos del usuario
     * /usuarios-empresa/{id}
     */
    actualizar(idUsuario: number, usuario: Partial<UsuarioEmpresa>): Observable<ApiResponse<void>> {
        return this.http.put<ApiResponse<void>>(`${this.apiUrl}/${idUsuario}`, usuario);
    }

    /**
     * Actualizar contraseña
     * /usuarios-empresa/{id}/password
     */
    actualizarPassword(idUsuario: number, password: string): Observable<ApiResponse<void>> {
        return this.http.put<ApiResponse<void>>(`${this.apiUrl}/${idUsuario}/password`, { password });
    }

    /**
     * Eliminar usuario de empresa
     * /usuarios-empresa/{idUsuario}/empresa/{idEmpresa}
     * 
     * No se puede eliminar si es el último usuario de la empresa
     */
    eliminar(idUsuario: number, idEmpresa: number): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${idUsuario}/empresa/${idEmpresa}`);
    }
}