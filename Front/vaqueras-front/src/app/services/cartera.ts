import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { backEnd } from '../app.config';
import { Cartera,Transaccion, RecargaRequest } from '../models/cartera_transaccion';
import { ApiResponse } from '../models/enums';

@Injectable({
    providedIn: 'root',
})
export class CarteraService {
    private apiUrl = `${backEnd.apiUrl}/cartera`;

    constructor(private http: HttpClient) { }

    //obtiene la cartera de un usuario
    getCartera(idUsuario: number): Observable<Cartera> {
        return this.http.get<ApiResponse<Cartera>>(`${this.apiUrl}/${idUsuario}`).pipe(
            map(response => response.data as Cartera)
        );
    }

    //realiza una recarga
    recargar(request: RecargaRequest): Observable<ApiResponse<Cartera>> {
        return this.http.post<ApiResponse<Cartera>>(
            `${this.apiUrl}/recargar`,
            request
        );
    }

    //obtiene el historial de transacciones
    getTransacciones(idUsuario: number): Observable<Transaccion[]> {
        return this.http.get<ApiResponse<Transaccion[]>>(
            `${this.apiUrl}/${idUsuario}/transacciones`
        ).pipe(
            map(response => response.data || [])
        );
    }

    //obtiene el saldo de la cartera 
    getSaldo(idUsuario: number): Observable<number> {
        return this.getCartera(idUsuario).pipe(
            map(cartera => cartera.saldo)
        );
    }

    //verifica si un usuario tiene saldo suficiente
    tieneSaldoSuficiente(idUsuario: number, monto: number): Observable<boolean> {
        return this.getSaldo(idUsuario).pipe(
            map(saldo => saldo >= monto)
        );
    }
}