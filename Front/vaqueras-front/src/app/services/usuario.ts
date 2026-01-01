import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';
import { Usuario } from '../models/usuario';
import { ApiResponse } from '../models/enums';

@Injectable({
    providedIn: 'root'
})
export class UsuarioService {
    private apiUrl = `${backEnd.apiUrl}/jugadores`;

    constructor(private http: HttpClient) { }

    buscarPorNickname(nickname: string): Observable<ApiResponse<Usuario[]>> {
        const params = new HttpParams().set('buscar', nickname);
        return this.http.get<ApiResponse<Usuario[]>>(`${this.apiUrl}`, { params });
    }

    //Obtener jugadores
    obtenerTodos(): Observable<ApiResponse<Usuario[]>> {
        return this.http.get<ApiResponse<Usuario[]>>(`${this.apiUrl}`);
    }

    
    obtenerPorId(id: number): Observable<ApiResponse<Usuario>> {
        return this.http.get<ApiResponse<Usuario>>(`${this.apiUrl}/${id}`);
    }

    
    actualizar(id: number, usuario: Partial<Usuario>): Observable<ApiResponse<any>> {
        return this.http.put<ApiResponse<any>>(`${this.apiUrl}/${id}`, usuario);
    }

    eliminar(id: number): Observable<ApiResponse<any>> {
        return this.http.delete<ApiResponse<any>>(`${this.apiUrl}/${id}`);
    }
}