import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';

export interface ImagenInfo {
    idImagen: number;
    nombreArchivo: string;
    tipoMime: string;
    tamanoBytes: number;
    esPortada: boolean;
}

export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data?: T;
}

@Injectable({
    providedIn: 'root'
})
export class ImagenService {
    private apiUrl = `${backEnd.apiUrl}/imagenes`;

    constructor(private http: HttpClient) { }

    //juegos

    subirImagenJuego(idJuego: number, file: File, esPortada: boolean = false): Observable<ApiResponse<number>> {
        const formData = new FormData();
        formData.append('imagen', file);
        formData.append('esPortada', esPortada.toString());

        return this.http.post<ApiResponse<number>>(`${this.apiUrl}/juego/${idJuego}`, formData);
    }

    getUrlImagenJuego(idImagen: number): string {
        return `${this.apiUrl}/juego/${idImagen}`;
    }

    getUrlPortadaJuego(idJuego: number): string {
        return `${this.apiUrl}/juego/${idJuego}/portada`;
    }

    listarImagenesJuego(idJuego: number): Observable<ApiResponse<ImagenInfo[]>> {
        return this.http.get<ApiResponse<ImagenInfo[]>>(`${this.apiUrl}/juego/${idJuego}/lista`);
    }

    establecerPortada(idJuego: number, idImagen: number): Observable<ApiResponse<void>> {
        return this.http.put<ApiResponse<void>>(`${this.apiUrl}/juego/${idJuego}/portada/${idImagen}`, {});
    }

    eliminarImagenJuego(idImagen: number): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/juego/${idImagen}`);
    }

    /**
     * USUARIOS (Avatares)
     */

    subirAvatar(idUsuario: number, file: File): Observable<ApiResponse<void>> {
        const formData = new FormData();
        formData.append('avatar', file);

        return this.http.post<ApiResponse<void>>(`${this.apiUrl}/usuario/${idUsuario}/avatar`, formData);
    }

    getUrlAvatar(idUsuario: number): string {
        return `${this.apiUrl}/usuario/${idUsuario}/avatar`;
    }

    eliminarAvatar(idUsuario: number): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/usuario/${idUsuario}/avatar`);
    }

    /**
     * BANNERS
     */

    subirImagenBanner(idBanner: number, file: File): Observable<ApiResponse<void>> {
        const formData = new FormData();
        formData.append('imagen', file);

        return this.http.post<ApiResponse<void>>(`${this.apiUrl}/banner/${idBanner}`, formData);
    }

    getUrlBanner(idBanner: number): string {
        return `${this.apiUrl}/banner/${idBanner}`;
    }

    eliminarImagenBanner(idBanner: number): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/banner/${idBanner}`);
    }
}