import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';
import { Respuesta, ApiResponse } from '../models/comentarios_respuestas';

@Injectable({
    providedIn: 'root'
})
export class RespuestaService {
    private apiUrl = `${backEnd.apiUrl}/respuestas`;

    constructor(private http: HttpClient) { }

    //   GET /respuestas/comentario/{idComentario} 
    obtenerPorComentario(idComentario: number): Observable<ApiResponse<Respuesta[]>> {
        return this.http.get<ApiResponse<Respuesta[]>>(`${this.apiUrl}/comentario/${idComentario}`);
    }

    
    obtenerPorUsuario(idUsuario: number): Observable<ApiResponse<Respuesta[]>> {
        return this.http.get<ApiResponse<Respuesta[]>>(`${this.apiUrl}/usuario/${idUsuario}`);
    }


    obtenerPorId(idRespuesta: number): Observable<ApiResponse<Respuesta>> {
        return this.http.get<ApiResponse<Respuesta>>(`${this.apiUrl}/${idRespuesta}`);
    }
    
    crear(respuesta: Respuesta): Observable<ApiResponse<Respuesta>> {
        return this.http.post<ApiResponse<Respuesta>>(this.apiUrl, respuesta);
    }

    actualizar(idRespuesta: number, contenido: string): Observable<ApiResponse<void>> {
        return this.http.put<ApiResponse<void>>(
            `${this.apiUrl}/${idRespuesta}`,
            { contenido }
        );
    }
    
    eliminar(idRespuesta: number): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${idRespuesta}`);
    }
}