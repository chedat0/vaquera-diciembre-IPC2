import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, of } from 'rxjs';
import { map, switchMap, catchError } from 'rxjs/operators';
import { backEnd } from '../app.config';
import { Comentario, CreateComentarioRequest, Respuesta, CreateRespuestaRequest } from '../models/comentarios_respuestas';
import { ApiResponse } from '../models/enums';
import { LicenciaService } from './licencia';

@Injectable({
    providedIn: 'root',
})
export class ComentarioService {
    private apiUrl = `${backEnd.apiUrl}/comentarios`;
    private apiUrlRespuestas = `${backEnd.apiUrl}/respuestas`;

    constructor(
        private http: HttpClient,
        private licenciaService: LicenciaService
    ) { }
    
    //   Crea un nuevo comentario
     
    createComentario(request: CreateComentarioRequest): Observable<ApiResponse<Comentario>> {
        // Primero verificar que el usuario tenga licencia
        return this.licenciaService.tieneLicencia(request.idUsuario, request.idJuego).pipe(
            switchMap(tieneLicencia => {
                if (!tieneLicencia) {
                    return of({
                        success: false,
                        message: 'Debes tener licencia del juego para poder comentar',
                        data: null
                    } as ApiResponse<Comentario>);
                }
                return this.http.post<ApiResponse<Comentario>>(this.apiUrl, request);
            }),
            catchError(error => {
                return of({
                    success: false,
                    message: 'Error al verificar licencia',
                    data: null
                } as ApiResponse<Comentario>);
            })
        );
    }

    //Obtiene comentarios de un juego
    getComentariosPorJuego(idJuego: number): Observable<Comentario[]> {
        return this.http.get<ApiResponse<Comentario[]>>(
            `${this.apiUrl}/juego/${idJuego}`
        ).pipe(
            map(response => response.data || []),
            // Cargar respuestas para cada comentario
            switchMap(comentarios => {
                if (comentarios.length === 0) {
                    return of([]);
                }

                const comentariosConRespuestas = comentarios.map(comentario =>
                    this.getRespuestasPorComentario(comentario.idComentario).pipe(
                        map(respuestas => ({
                            ...comentario,
                            respuestas
                        })),
                        catchError(() => of({
                            ...comentario,
                            respuestas: []
                        }))
                    )
                );

                return forkJoin(comentariosConRespuestas);
            })
        );
    }

    //Obtiene comentarios de un usuario
    getComentariosPorUsuario(idUsuario: number): Observable<Comentario[]> {
        return this.http.get<ApiResponse<Comentario[]>>(
            `${this.apiUrl}/usuario/${idUsuario}`
        ).pipe(
            map(response => response.data || [])
        );
    }

    //Actualiza comentario
    updateComentario(idComentario: number, contenido: string): Observable<ApiResponse<void>> {
        return this.http.put<ApiResponse<void>>(
            `${this.apiUrl}/${idComentario}`,
            { contenido }
        );
    }

    //Elimina comentario
    deleteComentario(idComentario: number): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(
            `${this.apiUrl}/${idComentario}`
        );
    }


    //Crea respuestas
    createRespuesta(
        request: CreateRespuestaRequest,
        idJuego: number
    ): Observable<ApiResponse<Respuesta>> {
        // Verificar que el usuario tenga licencia del juego
        return this.licenciaService.tieneLicencia(request.idUsuario, idJuego).pipe(
            switchMap(tieneLicencia => {
                if (!tieneLicencia) {
                    return of({
                        success: false,
                        message: 'Solo los usuarios con licencia del juego pueden responder comentarios',
                        data: null
                    } as ApiResponse<Respuesta>);
                }
                return this.http.post<ApiResponse<Respuesta>>(
                    this.apiUrlRespuestas,
                    request
                );
            }),
            catchError(error => {
                console.error('Error al verificar licencia para respuesta:', error);
                return of({
                    success: false,
                    message: 'Error al verificar permisos. Por favor intenta nuevamente.',
                    data: null
                } as ApiResponse<Respuesta>);
            })
        );
    }

    //Obtiene las respuestas de un comentario
    getRespuestasPorComentario(idComentario: number): Observable<Respuesta[]> {
        return this.http.get<ApiResponse<Respuesta[]>>(
            `${this.apiUrlRespuestas}/comentario/${idComentario}`
        ).pipe(
            map(response => response.data || [])
        );
    }

    //Actualiza una respuesta
    updateRespuesta(idRespuesta: number, contenido: string): Observable<ApiResponse<void>> {
        return this.http.put<ApiResponse<void>>(
            `${this.apiUrlRespuestas}/${idRespuesta}`,
            { contenido }
        );
    }

    //Elimina una respuesta
    deleteRespuesta(idRespuesta: number): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(
            `${this.apiUrlRespuestas}/${idRespuesta}`
        );
    }

    //valida si alguien puede responder un comentario
    puedeResponder(idUsuario: number, idJuego: number): Observable<boolean> {
        return this.licenciaService.puedeResponderComentario(idUsuario, idJuego);
    }
}