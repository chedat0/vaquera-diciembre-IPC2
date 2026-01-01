import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { JuegoService } from '../../../services/juego';
import { Juego } from '../../../models/juego';
import { ComentarioService } from '../../../services/comentario';
import { AuthService } from '../../../services/auth';

@Component({
    selector: 'app-juegos',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './juegos.html',
    styleUrl: './juegos.css'

})
export class MisJuegosEmpresa implements OnInit {
    juegos: Juego[] = [];
    juegoEditando: Juego = {} as Juego;
    mostrarModal = false;
    cargando = false;
    error = '';
    exito = '';
    usuarioActual: any;

    constructor(
        private juegoService: JuegoService,
        private comentarioService: ComentarioService,
        private authService: AuthService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.usuarioActual = this.authService.getCurrentUser();
        this.cargar();
    }

    cargar(): void {
        this.cargando = true;
        // Backend debe filtrar por idEmpresa del token
        this.juegoService.obtenerTodos().subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    // Filtrar juegos de esta empresa
                    this.juegos = res.data.filter(j => j.idEmpresa === this.usuarioActual.idEmpresa);
                }
                this.cargando = false;
            },
            error: () => { this.cargando = false; }
        });
    }

    crearJuego(): void {
        this.router.navigate(['/empresa/crear-juego']);
    }

    editar(juego: Juego): void {
        this.juegoEditando = { ...juego };
        this.mostrarModal = true;
    }

    cerrarModal(): void {
        this.mostrarModal = false;
    }

    guardarCambios(): void {
        this.juegoService.actualizar(this.juegoEditando.idJuego!, this.juegoEditando).subscribe({
            next: (res) => {
                if (res.success) {
                    this.exito = 'Juego actualizado';
                    this.cerrarModal();
                    this.cargar();
                    setTimeout(() => { this.exito = ''; }, 3000);
                } else {
                    this.error = res.message;
                }
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al actualizar';
            }
        });
    }

    juegoComentarios: Juego | null = null;
    mostrarModalComentarios = false;

    toggleComentarios(juego: Juego): void {
        this.juegoComentarios = juego;
        this.mostrarModalComentarios = true;
    }

    cerrarModalComentarios(): void {
        this.mostrarModalComentarios = false;
        this.juegoComentarios = null;
    }

    activarComentarios(): void {
        if (!this.juegoComentarios) return;

        // ✅ CORRECTO: variable 'visible' definida
        const visible = true;

        this.comentarioService.cambiarVisibilidadPorJuego(this.juegoComentarios.idJuego, visible).subscribe({
            next: (res) => {
                if (res.success) {
                    this.exito = 'Comentarios activados para todos los usuarios';
                    this.cerrarModalComentarios();
                    setTimeout(() => { this.exito = ''; }, 3000);
                }
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al activar comentarios';
            }
        });
    }

    desactivarComentarios(): void {
        if (!this.juegoComentarios) return;

        if (!confirm(`¿Desactivar comentarios de "${this.juegoComentarios.titulo}"?\n\nLos usuarios no podrán ver los comentarios existentes.`)) {
            return;
        }

        // ✅ CORRECTO: variable 'visible' definida
        const visible = false;

        this.comentarioService.cambiarVisibilidadPorJuego(this.juegoComentarios.idJuego, visible).subscribe({
            next: (res) => {
                if (res.success) {
                    this.exito = 'Comentarios desactivados. Los usuarios no verán los comentarios.';
                    this.cerrarModalComentarios();
                    setTimeout(() => { this.exito = ''; }, 3000);
                }
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al desactivar comentarios';
            }
        });
    }

    eliminar(juego: Juego): void {
        if (!confirm(`¿Eliminar "${juego.titulo}"? Esta acción no se puede deshacer.`)) return;

        this.juegoService.desactivarVenta(juego.idJuego!).subscribe({
            next: (res) => {
                if (res.success) {
                    this.exito = 'Juego eliminado';
                    this.cargar();
                    setTimeout(() => { this.exito = ''; }, 3000);
                } else {
                    this.error = res.message;
                }
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al eliminar';
            }
        });
    }
}