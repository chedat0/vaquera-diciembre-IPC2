import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InstalacionService } from '../../../services/instalacion-compra';
import { Instalacion } from '../../../models/instalacion';
import { AuthService } from '../../../services/auth';

@Component({
    selector: 'app-biblioteca',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './biblioteca.html',
    styleUrl: './biblioteca.css'
})
export class BibliotecaComponent implements OnInit {
    // Datos
    juegosDisponibles: Instalacion[] = [];
    juegosInstalados: Instalacion[] = [];

    // Filtros
    vistaActual: 'disponibles' | 'instalados' = 'disponibles';
    busqueda = '';

    // Estados
    cargando = false;
    procesando = false;
    error = '';
    exito = '';

    // Usuario actual
    usuarioActual: any;

    constructor(
        private instalacionService: InstalacionService,
        private authService: AuthService
    ) { }

    ngOnInit(): void {
        this.usuarioActual = this.authService.getCurrentUser();
        this.cargarDatos();
    }

    cargarDatos(): void {
        this.cargarDisponibles();
        this.cargarInstalados();
    }

    cargarDisponibles(): void {
        this.cargando = true;
        // GET /instalaciones/usuario/{id}/disponibles
        this.instalacionService.obtenerDisponibles(this.usuarioActual.idUsuario).subscribe({
            next: (response) => {
                if (response.success) {
                    this.juegosDisponibles = response.data || [];                    
                } else {
                    this.error = response.message;
                }
                this.cargando = false;
            },
            error: (err) => {
                this.error = 'Error al cargar juegos disponibles';
                this.cargando = false;
                console.error(err);
            }
        });
    }

    cargarInstalados(): void {
        // GET /instalaciones/usuario/{id}/instalados
        this.instalacionService.obtenerInstalados(this.usuarioActual.idUsuario).subscribe({
            next: (response) => {
                if (response.success) {
                    this.juegosInstalados = response.data || [];
                }
            },
            error: (err) => {
                console.error('Error al cargar instalados:', err);
            }
        });
    }

    instalar(juego: Instalacion): void {
        if (!confirm(`¿Instalar "${juego.tituloJuego}"?`)) {
            return;
        }

        this.procesando = true;
        this.error = '';

        const datos = {
            idUsuario: this.usuarioActual.idUsuario,
            idJuego: juego.idJuego,
            fechaEstado: new Date().toISOString().split('T')[0]  // YYYY-MM-DD
        };

        // El BACKEND valida el límite de 1 juego prestado instalado
        // POST /instalaciones
        this.instalacionService.instalar(datos).subscribe({
            next: (response) => {
                if (response.success) {
                    this.exito = `"${juego.tituloJuego}" instalado exitosamente`;
                    this.cargarDatos();
                    this.limpiarMensajes();
                } else {
                    // Backend retorna error si excede límite de prestados
                    this.error = response.message;
                }
                this.procesando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al instalar juego';
                this.procesando = false;
                console.error(err);
            }
        });
    }

    desinstalar(juego: Instalacion): void {
        if (!confirm(`¿Desinstalar "${juego.tituloJuego}"?`)) {
            return;
        }

        this.procesando = true;
        this.error = '';

        // DELETE /instalaciones/usuario/{idUsuario}/juego/{idJuego}
        this.instalacionService.desinstalar(
            this.usuarioActual.idUsuario,
            juego.idJuego
        ).subscribe({
            next: (response) => {
                if (response.success) {
                    this.exito = `"${juego.tituloJuego}" desinstalado`;
                    this.cargarDatos();
                    this.limpiarMensajes();
                } else {
                    this.error = response.message;
                }
                this.procesando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al desinstalar juego';
                this.procesando = false;
                console.error(err);
            }
        });
    }

    cambiarVista(vista: 'disponibles' | 'instalados'): void {
        this.vistaActual = vista;
        this.busqueda = '';
    }

    get juegosFiltrados(): Instalacion[] {
        const lista = this.vistaActual === 'disponibles'
            ? this.juegosDisponibles
            : this.juegosInstalados;

        if (!this.busqueda.trim()) {
            return lista;
        }

        const termino = this.busqueda.toLowerCase();
        return lista.filter(j =>
            j.tituloJuego?.toLowerCase().includes(termino)
        );
    }

    get juegosDisponiblesCount(): number {
        return this.juegosDisponibles.length;
    }

    get juegosInstaladosCount(): number {
        return this.juegosInstalados.length;
    }

    get juegosPrestadosInstalados(): number {
        return this.juegosInstalados.filter(j => j.esPrestado).length;
    }

    estaInstalado(juego: Instalacion): boolean {
        return juego.estado === 'INSTALADO';
    }

    limpiarMensajes(): void {
        setTimeout(() => {
            this.error = '';
            this.exito = '';
        }, 3000);
    }
}