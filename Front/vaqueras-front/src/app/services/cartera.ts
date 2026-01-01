import { Injectable } from '@angular/core';
import { HttpClient, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';
import { Cartera, Recarga, Transaccion, FiltroTransacciones, ApiResponse } from '../models/cartera_transaccion';

@Injectable({
    providedIn: 'root',
})

export class CarteraService {
    private apiUrl = `${backEnd.apiUrl}/cartera`;

    constructor(private http: HttpClient) { }
    
    obtenerCartera(idUsuario: number): Observable<ApiResponse<Cartera>> {
        return this.http.get<ApiResponse<Cartera>>(`${this.apiUrl}/${idUsuario}`);
    }

    crearCartera(idUsuario: number): Observable<ApiResponse<Cartera>> {
        return this.http.post<ApiResponse<Cartera>>(
            `${this.apiUrl}/crear`,
            { idUsuario }
        );
    }

    recargar(recarga: Recarga): Observable<ApiResponse<Cartera>> {
        return this.http.post<ApiResponse<Cartera>>(
            `${this.apiUrl}/recargar`,
            recarga
        );
    }
    
    obtenerTransacciones(idUsuario: number, filtros?: FiltroTransacciones): Observable<ApiResponse<Transaccion[]>> {
        let params = new HttpParams();

        if (filtros?.tipo) {
            params = params.set('tipo', filtros.tipo);
        }
        if (filtros?.fechaInicio) {
            params = params.set('fechaInicio', filtros.fechaInicio);
        }
        if (filtros?.fechaFin) {
            params = params.set('fechaFin', filtros.fechaFin);
        }

        return this.http.get<ApiResponse<Transaccion[]>>(
            `${this.apiUrl}/${idUsuario}/transacciones`,
            { params }
        );
    }
}