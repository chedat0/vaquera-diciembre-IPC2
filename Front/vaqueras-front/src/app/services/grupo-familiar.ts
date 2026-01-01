import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';
import { ApiResponse } from '../models/enums';

export interface GrupoFamiliar {
  idGrupo?: number;
  nombre: string;
  idCreador: number;
  nombreCreador?: string;
  cantidadMiembros?: number;
}

export interface MiembroGrupo {
  idUsuario: number;
  nickname: string;
  rol: string;  // 'CREADOR' o 'MIEMBRO'
}

export interface Invitacion {
  idInvitacion?: number;
  idGrupo: number;
  idUsuarioInvitado: number;
  idUsuarioInvitador: number;
  estado: string;  // 'PENDIENTE', 'ACEPTADA', 'RECHAZADA'
  fechaInvitacion: string;
  nombreGrupo?: string;
  nombreInvitador?: string;
  nombreInvitado?: string;
}


@Injectable({
  providedIn: 'root'
})
export class GrupoFamiliarService {
  private apiUrl = `${backEnd.apiUrl}/grupos`;
  private invitacionesUrl = `${backEnd.apiUrl}/invitaciones`;

  constructor(private http: HttpClient) { }

  //Grupos
  obtenerMisGrupos(idUsuario: number): Observable<ApiResponse<GrupoFamiliar[]>> {
    return this.http.get<ApiResponse<GrupoFamiliar[]>>(
      `${this.apiUrl}/usuario/${idUsuario}`
    );
  }


  obtenerPorId(idGrupo: number): Observable<ApiResponse<GrupoFamiliar>> {
    return this.http.get<ApiResponse<GrupoFamiliar>>(`${this.apiUrl}/${idGrupo}`);
  }


  crear(grupo: { nombre: string; idCreador: number }): Observable<ApiResponse<GrupoFamiliar>> {
    return this.http.post<ApiResponse<GrupoFamiliar>>(`${this.apiUrl}`, grupo);
  }

  actualizar(idGrupo: number, nombre: string): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.apiUrl}/${idGrupo}`, { nombre });
  }


  eliminar(idGrupo: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiUrl}/${idGrupo}`);
  }

  //Miembros del grupo
  obtenerMiembros(idGrupo: number): Observable<ApiResponse<MiembroGrupo[]>> {
    return this.http.get<ApiResponse<MiembroGrupo[]>>(
      `${this.apiUrl}/${idGrupo}/miembros`
    );
  }


  eliminarMiembro(idGrupo: number, idUsuario: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(
      `${this.apiUrl}/${idGrupo}/miembros/${idUsuario}`
    );
  }

  //Invitaciones
  enviarInvitacion(datos: {
    idGrupo: number;
    idUsuarioInvitado: number;
    idUsuarioInvitador: number;
    fechaInvitacion: string;
  }): Observable<ApiResponse<Invitacion>> {
    return this.http.post<ApiResponse<Invitacion>>(`${this.invitacionesUrl}`, datos);
  }


  obtenerInvitacionesPendientes(idUsuario: number): Observable<ApiResponse<Invitacion[]>> {
    return this.http.get<ApiResponse<Invitacion[]>>(
      `${this.invitacionesUrl}/usuario/${idUsuario}/pendientes`
    );
  }


  obtenerInvitacionesEnviadas(idUsuario: number): Observable<ApiResponse<Invitacion[]>> {
    return this.http.get<ApiResponse<Invitacion[]>>(
      `${this.invitacionesUrl}/usuario/${idUsuario}/enviadas`
    );
  }


  obtenerTodasInvitaciones(idUsuario: number): Observable<ApiResponse<Invitacion[]>> {
    return this.http.get<ApiResponse<Invitacion[]>>(
      `${this.invitacionesUrl}/usuario/${idUsuario}`
    );
  }


  aceptarInvitacion(idInvitacion: number): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(
      `${this.invitacionesUrl}/${idInvitacion}/aceptar`,
      {}  
    );
  }

  
  rechazarInvitacion(idInvitacion: number): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(
      `${this.invitacionesUrl}/${idInvitacion}/rechazar`,
      {}  
    );
  }


  cancelarInvitacion(idInvitacion: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(
      `${this.invitacionesUrl}/${idInvitacion}`
    );
  }
}