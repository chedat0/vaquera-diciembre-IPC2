import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';

export interface FiltroFechas {
  fechaInicio: string; // YYYY-MM-DD
  fechaFin: string;    // YYYY-MM-DD
}

export interface FiltroEmpresa extends FiltroFechas {
  idEmpresa: number;
  empresaNombre: string;
}

export interface FiltroUsuario extends FiltroFechas {
  idUsuario: number;
  usuarioNickname: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private apiUrl = `${backEnd.apiUrl}/reportes`;

  constructor(private http: HttpClient) { }

  /**
   * REPORTES ADMIN
   */

  /**
   * Ganancias Globales de la Plataforma
   * GET /reportes/ganancias-globales?fechaInicio=X&fechaFin=Y
   */
  generarGananciasGlobales(filtro: FiltroFechas): Observable<Blob> {
    let params = new HttpParams()
      .set('fechaInicio', filtro.fechaInicio)
      .set('fechaFin', filtro.fechaFin);

    return this.http.get(`${this.apiUrl}/ganancias-globales`, {
      params,
      responseType: 'blob'
    });
  }

  /**
   *  Top de Ventas por Categoría
   *  GET /reportes/top-ventas-categoria?fechaInicio=X&fechaFin=Y
   */
  generarTopVentasCategoria(filtro: FiltroFechas): Observable<Blob> {
    let params = new HttpParams()
      .set('fechaInicio', filtro.fechaInicio)
      .set('fechaFin', filtro.fechaFin);

    return this.http.get(`${this.apiUrl}/top-ventas-categoria`, {
      params,
      responseType: 'blob'
    });
  }

  /**
   * Top 10 Juegos Más Vendidos
   * GET /reportes/top-10-juegos
   */
  generarTop10Juegos(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/top-10-juegos`, {
      responseType: 'blob'
    });
  }

  /**
   * Ranking de Usuarios por Gasto
   *  GET /reportes/ranking-usuarios
   */
  generarRankingUsuarios(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/ranking-usuarios`, {
      responseType: 'blob'
    });
  }

  /**
   * REPORTES EMPRESA
   */

  /**
   * Ventas Propias de la Empresa
   * GET /reportes/ventas-empresa?idEmpresa=X&empresaNombre=Y&fechaInicio=Z&fechaFin=W
   */
  generarVentasEmpresa(filtro: FiltroEmpresa): Observable<Blob> {
    let params = new HttpParams()
      .set('idEmpresa', filtro.idEmpresa.toString())
      .set('empresaNombre', filtro.empresaNombre)
      .set('fechaInicio', filtro.fechaInicio)
      .set('fechaFin', filtro.fechaFin);

    return this.http.get(`${this.apiUrl}/ventas-empresa`, {
      params,
      responseType: 'blob'
    });
  }

  /**
   * Feedback de Juegos de la Empresa
   * GET /reportes/feedback-empresa?idEmpresa=X&empresaNombre=Y
   */
  generarFeedbackEmpresa(idEmpresa: number, empresaNombre: string): Observable<Blob> {
    let params = new HttpParams()
      .set('idEmpresa', idEmpresa.toString())
      .set('empresaNombre', empresaNombre);

    return this.http.get(`${this.apiUrl}/feedback-empresa`, {
      params,
      responseType: 'blob'
    });
  }

  /**
   * Top 5 Juegos Más Vendidos de la Empresa
   *  GET /reportes/top5-juegos-empresa?idEmpresa=X&empresaNombre=Y&fechaInicio=Z&fechaFin=W
   */
  generarTop5JuegosEmpresa(filtro: FiltroEmpresa): Observable<Blob> {
    let params = new HttpParams()
      .set('idEmpresa', filtro.idEmpresa.toString())
      .set('empresaNombre', filtro.empresaNombre)
      .set('fechaInicio', filtro.fechaInicio)
      .set('fechaFin', filtro.fechaFin);

    return this.http.get(`${this.apiUrl}/top5-juegos-empresa`, {
      params,
      responseType: 'blob'
    });
  }

  //Resportes del jugador

  /**
   * Historial de Gastos del Usuario
   *GET /reportes/historial-gastos?idUsuario=X&usuarioNickname=Y&fechaInicio=Z&fechaFin=W
   */
  generarHistorialGastos(filtro: FiltroUsuario): Observable<Blob> {
    let params = new HttpParams()
      .set('idUsuario', filtro.idUsuario.toString())
      .set('usuarioNickname', filtro.usuarioNickname)
      .set('fechaInicio', filtro.fechaInicio)
      .set('fechaFin', filtro.fechaFin);

    return this.http.get(`${this.apiUrl}/historial-gastos`, {
      params,
      responseType: 'blob'
    });
  }

  /**
   * Análisis de Biblioteca del Usuario
   * GET /reportes/analisis-biblioteca?idUsuario=X&usuarioNickname=Y
   */
  generarAnalisisBiblioteca(idUsuario: number, usuarioNickname: string): Observable<Blob> {
    let params = new HttpParams()
      .set('idUsuario', idUsuario.toString())
      .set('usuarioNickname', usuarioNickname);

    return this.http.get(`${this.apiUrl}/analisis-biblioteca`, {
      params,
      responseType: 'blob'
    });
  }

  /**
   * R10: Uso de Biblioteca Familiar
   * /reportes/biblioteca-familiar?idUsuario=X&usuarioNickname=Y
   */
  generarBibliotecaFamiliar(idUsuario: number, usuarioNickname: string): Observable<Blob> {
    let params = new HttpParams()
      .set('idUsuario', idUsuario.toString())
      .set('usuarioNickname', usuarioNickname);

    return this.http.get(`${this.apiUrl}/biblioteca-familiar`, {
      params,
      responseType: 'blob'
    });
  }

  //descargar el PDF
  descargarPDF(blob: Blob, nombreArchivo: string): void {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${nombreArchivo}.pdf`;
    link.click();
    window.URL.revokeObjectURL(url);
  }

  // abrir el PDF en nueva pestaña
  abrirPDFEnNuevaVentana(blob: Blob): void {
    const url = window.URL.createObjectURL(blob);
    window.open(url, '_blank');
  }
}