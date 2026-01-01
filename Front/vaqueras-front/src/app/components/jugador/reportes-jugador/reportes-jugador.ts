import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReporteService, FiltroUsuario } from '../../../services/reporte';
import { AuthService } from '../../../services/auth';

@Component({
    selector: 'app-reportes-jugador',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './reportes-jugador.html',
    styleUrl: './reportes-jugador.css'
})
export class ReportesJugadorComponent implements OnInit {

    filtroUsuario: FiltroUsuario = {
        idUsuario: 0,
        usuarioNickname: '',
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

        this.filtroUsuario.idUsuario = this.usuarioActual.idUsuario;
        this.filtroUsuario.usuarioNickname = this.usuarioActual.nickname || this.usuarioActual.nombre;

        const hoy = new Date();
        const haceMes = new Date();
        haceMes.setMonth(haceMes.getMonth() - 1);

        this.filtroUsuario.fechaInicio = this.formatearFecha(haceMes);
        this.filtroUsuario.fechaFin = this.formatearFecha(hoy);
    }

    /**
     *  Historial de Gastos
     */
    generarHistorialGastos(): void {
        if (!this.validarFechas()) return;

        this.procesando = true;
        this.error = '';

        this.reporteService.generarHistorialGastos(this.filtroUsuario).subscribe({
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
     *  Análisis de Biblioteca
     */
    generarAnalisisBiblioteca(): void {
        this.procesando = true;
        this.error = '';

        this.reporteService.generarAnalisisBiblioteca(
            this.filtroUsuario.idUsuario,
            this.filtroUsuario.usuarioNickname
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
     * Biblioteca Familiar
     */
    generarBibliotecaFamiliar(): void {
        this.procesando = true;
        this.error = '';

        this.reporteService.generarBibliotecaFamiliar(
            this.filtroUsuario.idUsuario,
            this.filtroUsuario.usuarioNickname
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

    validarFechas(): boolean {
        if (!this.filtroUsuario.fechaInicio || !this.filtroUsuario.fechaFin) {
            this.error = 'Debe seleccionar ambas fechas';
            return false;
        }

        const inicio = new Date(this.filtroUsuario.fechaInicio);
        const fin = new Date(this.filtroUsuario.fechaFin);

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