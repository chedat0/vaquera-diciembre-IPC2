import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PerfilJugadorService, PerfilJugador } from '../../../services/perfil-jugador';
import { AuthService } from '../../../services/auth';

@Component({
    selector: 'app-perfil',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './perfil.html',
    styleUrl: './perfil.css'
})
export class PerfilJugadorComponent implements OnInit {

    perfil: PerfilJugador | null = null;
    perfilEditado: Partial<PerfilJugador> = {};

    modoEdicion = false;
    cargando = false;
    procesando = false;
    error = '';
    exito = '';

    usuarioActual: any;

    constructor(
        private perfilService: PerfilJugadorService,
        private authService: AuthService
    ) { }

    ngOnInit(): void {
        this.usuarioActual = this.authService.getCurrentUser();
        this.cargarPerfil();
    }

    cargarPerfil(): void {
        this.cargando = true;
        this.perfilService.obtenerPerfil(this.usuarioActual.idUsuario).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.perfil = response.data;
                } else {
                    this.error = 'No se pudo cargar el perfil';
                }
                this.cargando = false;
            },
            error: (err) => {
                this.error = 'Error al cargar el perfil';
                this.cargando = false;
                console.error(err);
            }
        });
    }

    activarModoEdicion(): void {
        if (!this.perfil) return;

        this.modoEdicion = true;
        this.perfilEditado = {
            nickname: this.perfil.nickname,
            telefono: this.perfil.telefono,
            pais: this.perfil.pais,
            bibliotecaPublica: this.perfil.bibliotecaPublica
        };
    }

    cancelarEdicion(): void {
        this.modoEdicion = false;
        this.perfilEditado = {};
        this.error = '';
    }

    guardarCambios(): void {
        if (!this.perfil) return;

        // Validación básica
        if (!this.perfilEditado.nickname || this.perfilEditado.nickname.trim().length < 3) {
            this.error = 'El nickname debe tener al menos 3 caracteres';
            return;
        }

        this.procesando = true;
        this.error = '';

        this.perfilService.actualizarPerfil(this.perfil.idUsuario, this.perfilEditado).subscribe({
            next: (response) => {
                if (response.success) {
                    this.exito = 'Perfil actualizado exitosamente';
                    this.modoEdicion = false;
                    this.cargarPerfil(); // Recargar perfil actualizado
                    this.limpiarMensajes();
                } else {
                    this.error = response.message;
                }
                this.procesando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al actualizar el perfil';
                this.procesando = false;
                console.error(err);
            }
        });
    }

    calcularEdad(): number {
        if (!this.perfil?.fechaNacimiento) return 0;

        const hoy = new Date();
        const nacimiento = new Date(this.perfil.fechaNacimiento);
        let edad = hoy.getFullYear() - nacimiento.getFullYear();
        const mes = hoy.getMonth() - nacimiento.getMonth();

        if (mes < 0 || (mes === 0 && hoy.getDate() < nacimiento.getDate())) {
            edad--;
        }

        return edad;
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