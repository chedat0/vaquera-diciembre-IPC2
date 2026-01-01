import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';

export interface Empresa {
  idEmpresa?: number;
  nombre: string;
  descripcion: string;
  fechaCreacion?: Date;
  comisionEspecifica?: number;
  activa?: boolean;
}

export interface UsuarioEmpresa {
  idUsuarioEmpresa?: number;
  idEmpresa: number;
  correo: string;
  nombre: string;
  fechaNacimiento: Date;
  activo?: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
}

@Injectable({
  providedIn: 'root'
})
export class EmpresaService {
  private apiUrl = `${backEnd.apiUrl}/empresas`;
  private usuariosApiUrl = `${backEnd.apiUrl}/usuarios-empresa`;

  constructor(private http: HttpClient) { }

  // CRUD de Empresas
  obtenerTodas(): Observable<ApiResponse<Empresa[]>> {
    return this.http.get<ApiResponse<Empresa[]>>(`${this.apiUrl}`);
  }

  obtenerPorId(id: number): Observable<ApiResponse<Empresa>> {
    return this.http.get<ApiResponse<Empresa>>(`${this.apiUrl}/${id}`);
  }

  crear(empresa: Empresa): Observable<ApiResponse<Empresa>> {
    return this.http.post<ApiResponse<Empresa>>(`${this.apiUrl}`, empresa);
  }

  actualizar(id: number, empresa: Empresa): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.apiUrl}/${id}`, empresa);
  }

  eliminar(id: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiUrl}/${id}`);
  }

  cambiarEstado(id: number, activa: boolean): Observable<ApiResponse<any>> {
    return this.http.patch<ApiResponse<any>>(`${this.apiUrl}/${id}/estado`, { activa });
  }

  // Gestión de Comentarios
  cambiarVisibilidadComentarios(idJuego: number, visible: boolean): Observable<ApiResponse<any>> {
    return this.http.patch<ApiResponse<any>>(`${backEnd.apiUrl}/juegos/${idJuego}/comentarios-visibilidad`, { visible });
  }
}
