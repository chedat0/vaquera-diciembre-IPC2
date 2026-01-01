import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReporteService } from '../../../services/reporte';

@Component({
    selector: 'app-reportes-admin',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './reportes-admin.html',
    styleUrl: './reportes-admin.css'
})
export class ReportesAdminComponent implements OnInit {

    // Filtros para reportes con fechas
    filtroFechas = {
        fechaInicio: '',
        fechaFin: ''
    };

    procesando = false;
    error = '';
    exito = '';

    constructor(private reporteService: ReporteService) { }

    ngOnInit(): void {
        // Establecer fechas por defecto (último mes)
        const hoy = new Date();
        const haceMes = new Date();
        haceMes.setMonth(haceMes.getMonth() - 1);

        this.filtroFechas.fechaInicio = this.formatearFecha(haceMes);
        this.filtroFechas.fechaFin = this.formatearFecha(hoy);
    }

    /**
     * R1: Ganancias Globales
     */
    generarGananciasGlobales(): void {
        if (!this.validarFechas()) return;

        this.procesando = true;
        this.error = '';

        this.reporteService.generarGananciasGlobales(this.filtroFechas).subscribe({
            next: (blob) => {
                this.reporteService.abrirPDFEnNuevaVentana(blob);
                this.exito = 'Reporte generado exitosamente';
                this.procesando = false;
                this.limpiarMensajes();
            },
            error: (err) => {
                this.error = 'Error al generar el reporte';
                this.procesando = false;
                console.error(err);
            }
        });
    }

    /**
     * R2: Top Ventas por Categoría
     */
    generarTopVentasCategoria(): void {
        if (!this.validarFechas()) return;

        this.procesando = true;
        this.error = '';

        this.reporteService.generarTopVentasCategoria(this.filtroFechas).subscribe({
            next: (blob) => {
                this.reporteService.abrirPDFEnNuevaVentana(blob);
                this.exito = 'Reporte generado exitosamente';
                this.procesando = false;
                this.limpiarMensajes();
            },
            error: (err) => {
                this.error = 'Error al generar el reporte';
                this.procesando = false;
                console.error(err);
            }
        });
    }

    /**
     * R3: Top 10 Juegos
     */
    generarTop10Juegos(): void {
        this.procesando = true;
        this.error = '';

        this.reporteService.generarTop10Juegos().subscribe({
            next: (blob) => {
                this.reporteService.abrirPDFEnNuevaVentana(blob);
                this.exito = 'Reporte generado exitosamente';
                this.procesando = false;
                this.limpiarMensajes();
            },
            error: (err) => {
                this.error = 'Error al generar el reporte';
                this.procesando = false;
                console.error(err);
            }
        });
    }

    /**
     * R4: Ranking de Usuarios
     */
    generarRankingUsuarios(): void {
        this.procesando = true;
        this.error = '';

        this.reporteService.generarRankingUsuarios().subscribe({
            next: (blob) => {
                this.reporteService.abrirPDFEnNuevaVentana(blob);
                this.exito = 'Reporte generado exitosamente';
                this.procesando = false;
                this.limpiarMensajes();
            },
            error: (err) => {
                this.error = 'Error al generar el reporte';
                this.procesando = false;
                console.error(err);
            }
        });
    }

    // Validaciones y utilidades

    validarFechas(): boolean {
        if (!this.filtroFechas.fechaInicio || !this.filtroFechas.fechaFin) {
            this.error = 'Debe seleccionar ambas fechas';
            return false;
        }

        const inicio = new Date(this.filtroFechas.fechaInicio);
        const fin = new Date(this.filtroFechas.fechaFin);

        if (inicio > fin) {
            this.error = 'La fecha de inicio debe ser menor que la fecha fin';
            return false;
        }

        return true;
    }

    formatearFecha(date: Date): string {
        return date.toISOString().split('T')[0];
    }

    limpiarMensajes(): void {
        setTimeout(() => {
            this.error = '';
            this.exito = '';
        }, 3000);
    }
}