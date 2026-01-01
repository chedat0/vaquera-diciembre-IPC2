import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';
import { Banner,JuegoBanner } from '../models/banner';
import { ApiResponse } from '../models/enums';

@Injectable({
  providedIn: 'root'
})
export class BannerService {
  private apiUrl = `${backEnd.apiUrl}/banners`;

  constructor(private http: HttpClient) { }

  obtenerTodos(): Observable<ApiResponse<Banner[]>> {
    return this.http.get<ApiResponse<Banner[]>>(`${this.apiUrl}`);
  }

  obtenerActivos(): Observable<ApiResponse<Banner[]>> {
    return this.http.get<ApiResponse<Banner[]>>(`${this.apiUrl}/activos`);
  }

  obtenerPorId(id: number): Observable<ApiResponse<Banner>> {
    return this.http.get<ApiResponse<Banner>>(`${this.apiUrl}/${id}`);
  }

  
  obtenerMejorBalance(limite: number = 5): Observable<ApiResponse<JuegoBanner[]>> {
    return this.http.get<ApiResponse<JuegoBanner[]>>(
      `${this.apiUrl}/algoritmo/mejor-balance?limite=${limite}`
    );
  }

  crear(banner: Banner): Observable<ApiResponse<Banner>> {
    return this.http.post<ApiResponse<Banner>>(`${this.apiUrl}`, banner);
  }

  actualizarPosicion(id: number, posicion: number): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.apiUrl}/${id}/posicion`, { posicion });
  }

  actualizarEstado(id: number, activo: boolean): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.apiUrl}/${id}/estado`, { activo });
  }

  actualizarFechas(id: number, fechaInicio: string, fechaFin: string): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.apiUrl}/${id}/fechas`, { 
      fechaInicio, 
      fechaFin 
    });
  }

  eliminar(id: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiUrl}/${id}`);
  }
}



