import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Juego, JuegoResponse } from '../models/juego';

@Injectable({
    providedIn: 'root'
})
export class JuegoService {
    private apiUrl = 'http://localhost:8080/vaqueras_IPC2/juegos';

    constructor(private http: HttpClient) { }

    // Listar todos los juegos
    listarTodos(): Observable<JuegoResponse> {
        return this.http.get<JuegoResponse>(this.apiUrl);
    }

    // Buscar juego por ID
    buscarPorId(id: number): Observable<JuegoResponse> {
        const params = new HttpParams().set('id', id.toString());
        return this.http.get<JuegoResponse>(this.apiUrl, { params });
    }

    // Listar juegos por empresa
    listarPorEmpresa(idEmpresa: number): Observable<JuegoResponse> {
        const params = new HttpParams().set('idEmpresa', idEmpresa.toString());
        return this.http.get<JuegoResponse>(this.apiUrl, { params });
    }

    // Buscar por título
    buscarPorTitulo(titulo: string): Observable<JuegoResponse> {
        const params = new HttpParams().set('titulo', titulo);
        return this.http.get<JuegoResponse>(this.apiUrl, { params });
    }

    // Crear juego
    crear(juego: Juego): Observable<JuegoResponse> {
        return this.http.post<JuegoResponse>(this.apiUrl, juego);
    }

    // Actualizar juego
    actualizar(juego: Juego): Observable<JuegoResponse> {
        return this.http.put<JuegoResponse>(this.apiUrl, juego);
    }

    // Desactivar venta
    desactivarVenta(id: number): Observable<JuegoResponse> {
        const params = new HttpParams().set('id', id.toString());
        return this.http.delete<JuegoResponse>(this.apiUrl, { params });
    }
}