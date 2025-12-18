import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Categoria, CategoriaResponse } from '../models/categoria';

@Injectable({
    providedIn: 'root'
})
export class CategoriaService {
    private apiUrl = 'http://localhost:8080/vaqueras_IPC2/categorias';

    constructor(private http: HttpClient) { }

    listarTodas(): Observable<CategoriaResponse> {
        return this.http.get<CategoriaResponse>(this.apiUrl);
    }

    listarActivas(): Observable<CategoriaResponse> {
        const params = new HttpParams().set('activas', 'true');
        return this.http.get<CategoriaResponse>(this.apiUrl, { params });
    }

    buscarPorId(id: number): Observable<CategoriaResponse> {
        const params = new HttpParams().set('id', id.toString());
        return this.http.get<CategoriaResponse>(this.apiUrl, { params });
    }

    crear(categoria: Categoria): Observable<CategoriaResponse> {
        return this.http.post<CategoriaResponse>(this.apiUrl, categoria);
    }

    actualizar(categoria: Categoria): Observable<CategoriaResponse> {
        return this.http.put<CategoriaResponse>(this.apiUrl, categoria);
    }

    eliminar(id: number): Observable<CategoriaResponse> {
        const params = new HttpParams().set('id', id.toString());
        return this.http.delete<CategoriaResponse>(this.apiUrl, { params });
    }
}