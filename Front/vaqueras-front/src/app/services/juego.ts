import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs';
import { backEnd } from '../app.config';
import { Juego, CreateJuegoRequest } from '../models/juego';
import { Categoria } from '../models/categoria';
import { ApiResponse } from '../models/enums';

@Injectable({
    providedIn: 'root'
})
export class JuegoService {
    private apiUrl = `${backEnd.apiUrl}/juegos`;

    constructor(private http: HttpClient) { }

    // Listar todos los juegos
    listarTodos(): Observable<Juego[]> {
        return this.http.get<ApiResponse<Juego[]>>(this.apiUrl).pipe(
            map(response => response.data || [])
        );
    }

    //Obtiene juegos activos
    getJuegosActivos(): Observable<Juego[]> {
        return this.http.get<ApiResponse<Juego[]>>(`${this.apiUrl}/activos`).pipe(
            map(response => response.data || [])
        );
    }

    //Obtiene juego por id
    getJuego(idJuego: number): Observable<Juego> {
        return this.http.get<ApiResponse<Juego>>(`${this.apiUrl}/${idJuego}`).pipe(
            map(response => response.data as Juego)
        );
    }

    //Busca juegos por titulo
    buscarJuegos(titulo: string): Observable<Juego[]> {
        const params = new HttpParams().set('buscar', titulo);
        return this.http.get<ApiResponse<Juego[]>>(this.apiUrl, { params }).pipe(
            map(response => response.data || [])
        );
    }

    //Filtra juegos por categoria
    getJuegosPorCategoria(idCategoria: number): Observable<Juego[]> {
        return this.http.get<ApiResponse<Juego[]>>(
            `${this.apiUrl}/categoria/${idCategoria}`
        ).pipe(
            map(response => response.data || [])
        );
    }

    //Obtiene juegos de una empresa
    getJuegosPorEmpresa(idEmpresa: number): Observable<Juego[]> {
        return this.http.get<ApiResponse<Juego[]>>(
            `${this.apiUrl}/empresa/${idEmpresa}`
        ).pipe(
            map(response => response.data || [])
        );
    }

    //Crea un juego
    crearJuego(juego: CreateJuegoRequest): Observable<ApiResponse<Juego>> {
        return this.http.post<ApiResponse<Juego>>(this.apiUrl, juego);
    }

    //actualiza un juego
    actualizaJuego(idJuego: number, juego: Partial<Juego>): Observable<ApiResponse<void>> {
        return this.http.put<ApiResponse<void>>(`${this.apiUrl}/${idJuego}`, juego);
    }

    // Desactivar venta
    desactivarVenta(id: number): Observable<ApiResponse<Juego>> {
        const params = new HttpParams().set('id', id.toString());
        return this.http.delete<ApiResponse<Juego>>(this.apiUrl, { params });
    }
    
    //sube imagen de un juego
    subirImagen(idJuego: number, imagen: File): Observable<ApiResponse<string>> {
        const formData = new FormData();
        formData.append('imagen', imagen);
        return this.http.post<ApiResponse<string>>(
            `${this.apiUrl}/${idJuego}/imagen`,
            formData
        );
    }

    //Asigna categoria a un juego
    asignarCategorias(idJuego: number, categoriasIds: number[]): Observable<ApiResponse<void>> {
        return this.http.post<ApiResponse<void>>(
            `${this.apiUrl}/${idJuego}/categorias`,
            { categoriasIds }
        );
    }

    //Obtiene categorias de un juego
    getCategorias(idJuego: number): Observable<Categoria[]> {
        return this.http.get<ApiResponse<Categoria[]>>(
            `${this.apiUrl}/${idJuego}/categorias`
        ).pipe(
            map(response => response.data || [])
        );
    }
}