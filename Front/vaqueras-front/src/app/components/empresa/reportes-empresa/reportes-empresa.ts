import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReporteService, FiltroEmpresa } from '../../../services/reporte';
import { AuthService } from '../../../services/auth';

@Component({
    selector: 'app-reportes-empresa',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './reportes-empresa.html',
    styleUrl: './reportes-empresa.css'
})
export class ReportesEmpresa implements OnInit {

    filtroEmpresa: FiltroEmpresa = {
        idEmpresa: 0,
        empresaNombre: '',
        fechaInicio: '',
        fechaFin: ''
    };

    procesando = false;
    error = '';
    exito = '';

    usuarioActual: any;

    constructor(
        private reporteService: ReporteService,
        private authService: AuthService
    ) { }

    ngOnInit(): void {
        this.usuarioActual = this.authService.getCurrentUser();

        // Obtener datos de la empresa del usuario actual
        this.filtroEmpresa.idEmpresa = this.usuarioActual.idEmpresa || 1; // Ajustar según tu modelo
        this.filtroEmpresa.empresaNombre = this.usuarioActual.nombreEmpresa || 'Mi Empresa';

        // Establecer fechas por defecto
        const hoy = new Date();
        const haceMes = new Date();
        haceMes.setMonth(haceMes.getMonth() - 1);

        this.filtroEmpresa.fechaInicio = this.formatearFecha(haceMes);
        this.filtroEmpresa.fechaFin = this.formatearFecha(hoy);
    }

    /**
     *  Ventas de la Empresa
     */
    generarVentasEmpresa(): void {
        if (!this.validarFechas()) return;

        this.procesando = true;
        this.error = '';

        this.reporteService.generarVentasEmpresa(this.filtroEmpresa).subscribe({
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
     *  Feedback de Juegos
     */
    generarFeedbackEmpresa(): void {
        this.procesando = true;
        this.error = '';

        this.reporteService.generarFeedbackEmpresa(
            this.filtroEmpresa.idEmpresa,
            this.filtroEmpresa.empresaNombre
        ).subscribe({
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
     *  Top 5 Juegos de la Empresa
     */
    generarTop5JuegosEmpresa(): void {
        if (!this.validarFechas()) return;

        this.procesando = true;
        this.error = '';

        this.reporteService.generarTop5JuegosEmpresa(this.filtroEmpresa).subscribe({
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

    validarFechas(): boolean {
        if (!this.filtroEmpresa.fechaInicio || !this.filtroEmpresa.fechaFin) {
            this.error = 'Debe seleccionar ambas fechas';
            return false;
        }

        const inicio = new Date(this.filtroEmpresa.fechaInicio);
        const fin = new Date(this.filtroEmpresa.fechaFin);

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