import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CarteraService} from '../../../services/cartera';
import { Cartera,Transaccion } from '../../../models/cartera_transaccion';
import { AuthService } from '../../../services/auth';

@Component({
    selector: 'app-cartera',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './cartera.html',
    styleUrl: './cartera.css'
})
export class CarteraComponent implements OnInit {
    cartera: Cartera | null = null;
    transacciones: Transaccion[] = [];
    montoRecarga = 0;

    cargando = false;
    cargandoTransacciones = false;
    procesando = false;
    error = '';
    exito = '';

    mostrarModalRecarga = false;

    // Filtros de transacciones
    filtroTipo: 'TODAS' | 'RECARGA' | 'COMPRA' = 'TODAS';
    mostrarFiltros = false;

    usuarioActual: any;

    // Montos predefinidos para recarga rápida
    montosRapidos = [10, 25, 50, 100, 200];

    constructor(
        private carteraService: CarteraService,
        private authService: AuthService
    ) { }

    ngOnInit(): void {
        this.usuarioActual = this.authService.getCurrentUser();
        this.cargarCartera();
        this.cargarTransacciones();
    }

    cargarCartera(): void {
        this.cargando = true;
        this.carteraService.obtenerCartera(this.usuarioActual.idUsuario).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.cartera = response.data;
                } else {                    
                    this.crearCartera();
                }
                this.cargando = false;
            },
            error: (err) => {
                // Si da 404, crear cartera
                if (err.status === 404) {
                    this.crearCartera();
                } else {
                    this.error = 'Error al cargar cartera';
                    this.cargando = false;
                }
                console.error(err);
            }
        });
    }

    crearCartera(): void {
        this.carteraService.crearCartera(this.usuarioActual.idUsuario).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.cartera = response.data;
                    this.exito = 'Cartera creada exitosamente';
                    this.limpiarMensajes();
                }
                this.cargando = false;
            },
            error: (err) => {
                this.error = 'Error al crear cartera';
                this.cargando = false;
                console.error(err);
            }
        });
    }

    cargarTransacciones(): void {
        this.cargandoTransacciones = true;

        const filtros = this.filtroTipo !== 'TODAS'
            ? { tipo: this.filtroTipo }
            : undefined;

        this.carteraService.obtenerTransacciones(
            this.usuarioActual.idUsuario,
            filtros
        ).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.transacciones = response.data;
                } else {
                    this.transacciones = [];
                }
                this.cargandoTransacciones = false;
            },
            error: (err) => {
                console.error('Error al cargar transacciones:', err);
                this.transacciones = [];
                this.cargandoTransacciones = false;
            }
        });
    }

    cambiarFiltro(tipo: 'TODAS' | 'RECARGA' | 'COMPRA'): void {
        this.filtroTipo = tipo;
        this.cargarTransacciones();
    }

    abrirModalRecarga(): void {
        this.montoRecarga = 0;
        this.mostrarModalRecarga = true;
        this.error = '';
    }

    seleccionarMontoRapido(monto: number): void {
        this.montoRecarga = monto;
    }

    recargar(): void {        
        if (this.montoRecarga <= 0) {
            this.error = 'El monto debe ser mayor a 0';
            return;
        }

        this.procesando = true;
        this.error = '';

        const recarga = {
            idUsuario: this.usuarioActual.idUsuario,
            monto: this.montoRecarga,
            fecha: new Date().toISOString().split('T')[0] // YYYY-MM-DD
        };
        
        this.carteraService.recargar(recarga).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.cartera = response.data;
                    this.exito = `Recarga de $${this.montoRecarga.toFixed(2)} realizada exitosamente`;
                    this.mostrarModalRecarga = false;
                    this.montoRecarga = 0;
                    
                    this.cargarTransacciones();

                    this.limpiarMensajes();
                } else {
                    this.error = response.message;
                }
                this.procesando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al procesar recarga';
                this.procesando = false;
                console.error(err);
            }
        });
    }

    cancelarRecarga(): void {
        this.mostrarModalRecarga = false;
        this.montoRecarga = 0;
        this.error = '';
    }


    get transaccionesFiltradas(): Transaccion[] {
        return this.transacciones;
    }

    get totalRecargas(): number {
        return this.transacciones
            .filter(t => t.tipo === 'RECARGA')
            .reduce((sum, t) => sum + t.monto, 0);
    }

    get totalCompras(): number {
        return this.transacciones
            .filter(t => t.tipo === 'COMPRA')
            .reduce((sum, t) => sum + t.monto, 0);
    }

    esRecarga(transaccion: Transaccion): boolean {
        return transaccion.tipo === 'RECARGA';
    }

    esCompra(transaccion: Transaccion): boolean {
        return transaccion.tipo === 'COMPRA';
    }

    formatearFecha(fecha: string): string {
        const date = new Date(fecha);
        return date.toLocaleDateString('es-ES', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }

    limpiarMensajes(): void {
        setTimeout(() => {
            this.error = '';
            this.exito = '';
        }, 3000);
    }
}