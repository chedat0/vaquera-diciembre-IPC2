import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';
import { Categoria } from '../models/categoria';
import { ApiResponse } from '../models/enums';


@Injectable({
    providedIn: 'root'
})
export class CategoriaService {
    private apiUrl = `${backEnd.apiUrl}/categorias`;

    constructor(private http: HttpClient) { }
    
    obtenerTodas(): Observable<ApiResponse<Categoria[]>> {
        return this.http.get<ApiResponse<Categoria[]>>(`${this.apiUrl}`);
    }
    
    obtenerActivas(): Observable<ApiResponse<Categoria[]>> {
        return this.http.get<ApiResponse<Categoria[]>>(`${this.apiUrl}/activas`);
    }
    
    obtenerPorId(id: number): Observable<ApiResponse<Categoria>> {
        return this.http.get<ApiResponse<Categoria>>(`${this.apiUrl}/${id}`);
    }

    crear(categoria: Categoria): Observable<ApiResponse<Categoria>> {
        return this.http.post<ApiResponse<Categoria>>(`${this.apiUrl}`, categoria);
    }

    actualizar(categoria: Categoria): Observable<ApiResponse<any>> {
        return this.http.put<ApiResponse<any>>(`${this.apiUrl}/${categoria.idCategoria}`, categoria);
    }
    
    eliminar(id: number): Observable<ApiResponse<any>> {
        return this.http.delete<ApiResponse<any>>(`${this.apiUrl}/${id}`);
    }

    cambiarEstado( activa: boolean): Observable<ApiResponse<any>> {
        return this.actualizar( { activa } as Categoria);
    }
}