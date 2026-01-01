import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';
import { Comentario, Respuesta,PromedioCalificacion,ApiResponse } from '../models/comentarios_respuestas';

@Injectable({
    providedIn: 'root'
})
export class ComentarioService {
    private apiUrl = `${backEnd.apiUrl}/comentarios`;

    constructor(private http: HttpClient) { }

    obtenerPorJuego(idJuego: number): Observable<ApiResponse<Comentario[]>> {
        return this.http.get<ApiResponse<Comentario[]>>(`${this.apiUrl}/juego/${idJuego}`);
    }

    
    obtenerPromedio(idJuego: number): Observable<ApiResponse<PromedioCalificacion>> {
        return this.http.get<ApiResponse<PromedioCalificacion>>(`${this.apiUrl}/juego/${idJuego}/promedio`);
    }
    
    obtenerPorUsuario(idUsuario: number): Observable<ApiResponse<Comentario[]>> {
        return this.http.get<ApiResponse<Comentario[]>>(`${this.apiUrl}/usuario/${idUsuario}`);
    }
    
    obtenerPorId(idComentario: number): Observable<ApiResponse<Comentario>> {
        return this.http.get<ApiResponse<Comentario>>(`${this.apiUrl}/${idComentario}`);
    }

    
    crear(comentario: Comentario): Observable<ApiResponse<Comentario>> {
        return this.http.post<ApiResponse<Comentario>>(this.apiUrl, comentario);
    }
    
    actualizar(idComentario: number, contenido: string): Observable<ApiResponse<void>> {
        return this.http.put<ApiResponse<void>>(
            `${this.apiUrl}/${idComentario}`,
            { contenido }
        );
    }


    cambiarVisibilidad(idComentario: number, visible: boolean): Observable<ApiResponse<void>> {
        return this.http.put<ApiResponse<void>>(
            `${this.apiUrl}/${idComentario}/visibilidad`,
            { visible }
        );
    }


    cambiarVisibilidadPorJuego(idJuego: number, visible: boolean): Observable<ApiResponse<void>> {
        return this.http.put<ApiResponse<void>>(
            `${this.apiUrl}/juego/${idJuego}/visibilidad`,
            { visible }
        );
    }


    eliminar(idComentario: number): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${idComentario}`);
    }
}