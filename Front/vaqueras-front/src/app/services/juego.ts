import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs';
import { backEnd } from '../app.config';
import { CreateJuegoRequest, Juego } from '../models/juego';
import { Categoria } from '../models/categoria';
import { ApiResponse } from '../models/enums';

@Injectable({
    providedIn: 'root'
})

export class JuegoService {
    private apiUrl = `${backEnd.apiUrl}/juegos`;

    constructor(private http: HttpClient) { }

    
    obtenerTodos(): Observable<ApiResponse<Juego[]>> {
        return this.http.get<ApiResponse<Juego[]>>(`${this.apiUrl}`);
    }
    
    obtenerActivos(): Observable<ApiResponse<Juego[]>> {
        return this.http.get<ApiResponse<Juego[]>>(`${this.apiUrl}/activos`);
    }
    
    obtenerPorId(idJuego: number): Observable<ApiResponse<Juego>> {
        return this.http.get<ApiResponse<Juego>>(`${this.apiUrl}/${idJuego}`);
    }
    
    buscarPorTitulo(titulo: string): Observable<ApiResponse<Juego[]>> {
        const params = new HttpParams().set('titulo', titulo);
        return this.http.get<ApiResponse<Juego[]>>(`${this.apiUrl}`, { params });
    }
    
    obtenerPorEmpresa(idEmpresa: number): Observable<ApiResponse<Juego[]>> {
        return this.http.get<ApiResponse<Juego[]>>(`${this.apiUrl}/empresa/${idEmpresa}`);
    }
    
    crear(juego: CreateJuegoRequest): Observable<ApiResponse<Juego>> {
        return this.http.post<ApiResponse<Juego>>(`${this.apiUrl}`, juego);
    }
    
    actualizar(idJuego: number, juego: Partial<Juego>): Observable<ApiResponse<any>> {
        return this.http.put<ApiResponse<any>>(`${this.apiUrl}/${idJuego}`, juego);
    }
    
    desactivarVenta(id: number): Observable<ApiResponse<any>> {
        return this.http.delete<ApiResponse<any>>(`${this.apiUrl}/${id}`);
    }
    
    subirImagen(idJuego: number, imagen: File): Observable<ApiResponse<string>> {
        const formData = new FormData();
        formData.append('imagen', imagen);
        return this.http.post<ApiResponse<string>>(
            `${this.apiUrl}/imagenes/juego/${idJuego}`,
            formData
        );
    }
    
    asignarCategorias(idJuego: number, categoriasIds: number[]): Observable<ApiResponse<any>> {
        return this.http.post<ApiResponse<any>>(
            `${this.apiUrl}/${idJuego}/categorias`,
            { categoriasIds }
        );
    }
    
    obtenerCategorias(idJuego: number): Observable<ApiResponse<Categoria[]>> {
        return this.http.get<ApiResponse<Categoria[]>>(
            `${this.apiUrl}/${idJuego}/categorias`
        );
    }
}