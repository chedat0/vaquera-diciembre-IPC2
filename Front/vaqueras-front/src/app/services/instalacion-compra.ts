import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';
import { Instalacion } from '../models/instalacion';
import { Compra} from '../models/compra';
import { ApiResponse } from '../models/enums';


@Injectable({
  providedIn: 'root'
})
export class InstalacionService {
  private apiUrl = `${backEnd.apiUrl}/instalaciones`;

  constructor(private http: HttpClient) { }

  
  obtenerInstalaciones(idUsuario: number): Observable<ApiResponse<Instalacion[]>> {
    return this.http.get<ApiResponse<Instalacion[]>>(
      `${this.apiUrl}/usuario/${idUsuario}`
    );
  }
  
  obtenerInstalados(idUsuario: number): Observable<ApiResponse<Instalacion[]>> {
    return this.http.get<ApiResponse<Instalacion[]>>(
      `${this.apiUrl}/usuario/${idUsuario}/instalados`
    );
  }
  
  obtenerDisponibles(idUsuario: number): Observable<ApiResponse<Instalacion[]>> {
    return this.http.get<ApiResponse<Instalacion[]>>(
      `${this.apiUrl}/usuario/${idUsuario}/disponibles`
    );
  }

  
  instalar(datos: {
    idUsuario: number;
    idJuego: number;
    fechaEstado: string; // YYYY-MM-DD
  }): Observable<ApiResponse<Instalacion>> {
    return this.http.post<ApiResponse<Instalacion>>(`${this.apiUrl}`, datos);
  }

  
  desinstalar(idUsuario: number, idJuego: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(
      `${this.apiUrl}/usuario/${idUsuario}/juego/${idJuego}`
    );
  }
}


@Injectable({
  providedIn: 'root'
})
export class CompraService {
  private apiUrl = `${backEnd.apiUrl}/compras`;

  constructor(private http: HttpClient) { }

  comprar(compra: {
    idUsuario: number;
    idJuego: number;
    fechaCompra: string; // YYYY-MM-DD
  }): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}`, compra);
  }
  
}
