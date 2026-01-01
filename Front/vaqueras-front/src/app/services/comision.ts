import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';
import { ApiResponse } from '../models/enums';
import { ComisionEmpresa, ComisionGlobal, EmpresaConComision, HistorialComision } from '../models/comision';



@Injectable({
    providedIn: 'root'
})
export class ComisionService {
    private apiUrl = `${backEnd.apiUrl}/comisiones`;

    constructor(private http: HttpClient) { }

    // Comisión Global
    obtenerComisionGlobal(): Observable<ApiResponse<ComisionGlobal>> {
        return this.http.get<ApiResponse<ComisionGlobal>>(`${this.apiUrl}/global`);
    }

    obtenerHistorialGlobal(): Observable<ApiResponse<HistorialComision[]>> {
    return this.http.get<ApiResponse<HistorialComision[]>>(`${this.apiUrl}/global/historial`);
    }

    actualizarComisionGlobal(porcentaje: number): Observable<ApiResponse<any>> {
        return this.http.put<ApiResponse<any>>(`${this.apiUrl}/global`, { porcentaje });
    }

    // Comisiones por Empresa
    obtenerComisionesEmpresas(): Observable<ApiResponse<ComisionEmpresa[]>> {
        return this.http.get<ApiResponse<ComisionEmpresa[]>>(`${this.apiUrl}/empresas`);
    }

    obtenerComisionEmpresa(idEmpresa: number): Observable<ApiResponse<ComisionEmpresa>> {
        return this.http.get<ApiResponse<ComisionEmpresa>>(`${this.apiUrl}/empresa/${idEmpresa}`);
    }

    asignarComisionEmpresa(idEmpresa: number, porcentaje: number): Observable<ApiResponse<any>> {
        return this.http.post<ApiResponse<any>>(`${this.apiUrl}/empresa`, {
            idEmpresa,
            porcentajeEspecifico: porcentaje
        });
    }

    actualizarComisionEmpresa(idEmpresa: number, porcentaje: number): Observable<ApiResponse<any>> {
        return this.http.put<ApiResponse<any>>(`${this.apiUrl}/empresa/${idEmpresa}`, {
            porcentajeEspecifico: porcentaje
        });
    }

    eliminarComisionEmpresa(idEmpresa: number): Observable<ApiResponse<any>> {
        return this.http.delete<ApiResponse<any>>(`${this.apiUrl}/empresa/${idEmpresa}`);
    }
}