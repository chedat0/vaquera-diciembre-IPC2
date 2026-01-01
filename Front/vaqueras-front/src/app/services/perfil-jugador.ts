import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';

export interface PerfilJugador {
    idUsuario: number;
    nickname: string;
    correo: string;
    fechaNacimiento: string; // YYYY-MM-DD
    telefono?: string;
    pais?: string;
    bibliotecaPublica: boolean;
    idRol: number;
}

export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data?: T;
}

@Injectable({
    providedIn: 'root'
})
export class PerfilJugadorService {
    private apiUrl = `${backEnd.apiUrl}/jugadores`;

    constructor(private http: HttpClient) { }
    
    obtenerPerfil(idUsuario: number): Observable<ApiResponse<PerfilJugador>> {
        return this.http.get<ApiResponse<PerfilJugador>>(`${this.apiUrl}/${idUsuario}`);
    }

    /**
     * Buscar jugadores por nickname
     *  /jugadores?buscar={nickname}
     */
    buscarJugadores(nickname: string): Observable<ApiResponse<PerfilJugador[]>> {
        let params = new HttpParams().set('buscar', nickname);
        return this.http.get<ApiResponse<PerfilJugador[]>>(this.apiUrl, { params });
    }
    
    listarTodos(): Observable<ApiResponse<PerfilJugador[]>> {
        return this.http.get<ApiResponse<PerfilJugador[]>>(this.apiUrl);
    }
    
    actualizarPerfil(idUsuario: number, perfil: Partial<PerfilJugador>): Observable<ApiResponse<void>> {
        return this.http.put<ApiResponse<void>>(`${this.apiUrl}/${idUsuario}`, perfil);
    }

    // Eliminar cuenta (solo admin puede hacer esto)

    eliminarCuenta(idUsuario: number): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${idUsuario}`);
    }
}