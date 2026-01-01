import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';
import { Compra, ResultadoCompra, ApiResponse } from '../models/compra';


@Injectable({
    providedIn: 'root'
})
export class CompraService {
    private apiUrl = `${backEnd.apiUrl}/compras`;

    constructor(private http: HttpClient) { }

    comprar(compra: Compra): Observable<ApiResponse<ResultadoCompra>> {
        return this.http.post<ApiResponse<ResultadoCompra>>(`${this.apiUrl}`, compra);
    }
}