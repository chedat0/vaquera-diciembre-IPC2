import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { backEnd } from '../app.config';
import { Licencia, CompraJuegoRequest } from '../models/licencia';
import { ApiResponse } from '../models/enums';

@Injectable({
    providedIn: 'root',
})
export class LicenciaService {
    private apiUrl = `${backEnd.apiUrl}/licencias`;

    constructor(private http: HttpClient) { }
    
    getLicenciasPorUsuario(idUsuario: number): Observable<Licencia[]> {
        return this.http.get<ApiResponse<Licencia[]>>(
            `${this.apiUrl}/usuario/${idUsuario}`
        ).pipe(
            map(response => response.data || [])
        );
    }

    //Obtiene las licencias de un juego
    getLicenciasPorJuego(idJuego: number): Observable<Licencia[]> {
        return this.http.get<ApiResponse<Licencia[]>>(
            `${this.apiUrl}/juego/${idJuego}`
        ).pipe(
            map(response => response.data || [])
        );
    }

    //Verifica si un usuario tiene licencia de un juego
    tieneLicencia(idUsuario: number, idJuego: number): Observable<boolean> {
        return this.http.get<ApiResponse<boolean>>(
            `${this.apiUrl}/verificar/${idUsuario}/${idJuego}`
        ).pipe(
            map(response => response.data === true)
        );
    }

    //Comprar juego y crear licencia
    comprarJuego(request: CompraJuegoRequest): Observable<ApiResponse<Licencia>> {
        return this.http.post<ApiResponse<Licencia>>(
            `${this.apiUrl}/comprar`,
            request
        );
    }

    //Obtiene conteo de ventas de un juego
    getVentasJuego(idJuego: number): Observable<number> {
        return this.http.get<ApiResponse<number>>(
            `${this.apiUrl}/ventas/${idJuego}`
        ).pipe(
            map(response => response.data || 0)
        );
    }

    //Obtiene el totas de juegos de un usuario
    getTotalJuegosUsuario(idUsuario: number): Observable<number> {
        return this.http.get<ApiResponse<number>>(
            `${this.apiUrl}/total/${idUsuario}`
        ).pipe(
            map(response => response.data || 0)
        );
    }

    //Valida que un usuario pueda responder un comentario
    puedeResponderComentario(idUsuario: number, idJuego: number): Observable<boolean> {
        return this.tieneLicencia(idUsuario, idJuego);
    }
}