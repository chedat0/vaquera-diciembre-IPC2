import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ComentarioService} from '../../../services/comentario';
import { Comentario, PromedioCalificacion, Respuesta } from '../../../models/comentarios_respuestas';
import { RespuestaService} from '../../../services/respuesta';
import { AuthService } from '../../../services/auth';

@Component({
    selector: 'app-comentarios-juego',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './comentarios.html',
    styleUrl: './comentarios.css'
})

export class Comentarios implements OnInit {
    Math = Math;
    @Input() idJuego!: number;
    @Input() nombreJuego = '';

    comentarios: Comentario[] = [];
    promedio: PromedioCalificacion | null = null;

    // Formulario de nuevo comentario
    nuevoComentario: Comentario = {
        idJuego: 0,
        idUsuario: 0,
        contenido: '',
        calificacion: 5,
        fecha: ''
    };

    // Formulario de nueva respuesta
    nuevaRespuesta: { [idComentario: number]: string } = {};

    // Control de UI
    mostrarFormularioComentario = false;
    comentarioExpandido: { [id: number]: boolean } = {};
    editandoComentario: { [id: number]: boolean } = {};
    editandoRespuesta: { [id: number]: boolean } = {};
    contenidoEditado: { [id: number]: string } = {};

    cargando = false;
    procesando = false;
    error = '';
    exito = '';

    usuarioActual: any;
    tieneLicencia = false; // Se valida en backend, pero se muestra en UI

    constructor(
        private comentarioService: ComentarioService,
        private respuestaService: RespuestaService,
        private authService: AuthService
    ) { }

    ngOnInit(): void {
        this.usuarioActual = this.authService.getCurrentUser();
        this.cargarComentarios();
        this.cargarPromedio();
        // En un caso real, verificarías si el usuario tiene el juego
        // Por ahora asumimos que sí para mostrar el formulario
        this.tieneLicencia = true;
    }

    cargarComentarios(): void {
        this.cargando = true;
        this.comentarioService.obtenerPorJuego(this.idJuego).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.comentarios = response.data;
                } else {
                    this.comentarios = [];
                }
                this.cargando = false;
            },
            error: (err) => {
                console.error('Error al cargar comentarios:', err);
                this.comentarios = [];
                this.cargando = false;
            }
        });
    }

    cargarPromedio(): void {
        this.comentarioService.obtenerPromedio(this.idJuego).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.promedio = response.data;
                }
            },
            error: (err) => {
                console.error('Error al cargar promedio:', err);
            }
        });
    }

    abrirFormularioComentario(): void {
        this.mostrarFormularioComentario = true;
        this.nuevoComentario = {
            idJuego: this.idJuego,
            idUsuario: this.usuarioActual.idUsuario,
            contenido: '',
            calificacion: 5,
            fecha: new Date().toISOString().split('T')[0]
        };
    }

    crearComentario(): void {
        // Validación UI
        if (!this.nuevoComentario.contenido.trim()) {
            this.error = 'El comentario no puede estar vacío';
            return;
        }

        if (this.nuevoComentario.calificacion < 1 || this.nuevoComentario.calificacion > 5) {
            this.error = 'La calificación debe estar entre 1 y 5 estrellas';
            return;
        }

        this.procesando = true;
        this.error = '';

        // Backend valida que el usuario tenga licencia
        this.comentarioService.crear(this.nuevoComentario).subscribe({
            next: (response) => {
                if (response.success) {
                    this.exito = 'Comentario publicado exitosamente';
                    this.mostrarFormularioComentario = false;
                    this.cargarComentarios();
                    this.cargarPromedio();
                    this.limpiarMensajes();
                } else {
                    this.error = response.message;
                }
                this.procesando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al publicar comentario';
                this.procesando = false;
            }
        });
    }

    cancelarComentario(): void {
        this.mostrarFormularioComentario = false;
        this.nuevoComentario.contenido = '';
    }

    // Respuestas

    toggleRespuestas(idComentario: number): void {
        this.comentarioExpandido[idComentario] = !this.comentarioExpandido[idComentario];
    }

    crearRespuesta(comentario: Comentario): void {
        const contenido = this.nuevaRespuesta[comentario.idComentario!];

        if (!contenido || !contenido.trim()) {
            this.error = 'La respuesta no puede estar vacía';
            return;
        }

        this.procesando = true;
        this.error = '';

        const respuesta: Respuesta = {
            idComentario: comentario.idComentario!,
            idUsuario: this.usuarioActual.idUsuario,
            contenido: contenido.trim(),
            fecha: new Date().toISOString().split('T')[0]
        };

        // Backend valida que el usuario tenga licencia
        this.respuestaService.crear(respuesta).subscribe({
            next: (response) => {
                if (response.success) {
                    this.nuevaRespuesta[comentario.idComentario!] = '';
                    this.cargarComentarios(); // Recargar para mostrar la nueva respuesta
                    this.exito = 'Respuesta publicada';
                    this.limpiarMensajes();
                } else {
                    this.error = response.message;
                }
                this.procesando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al publicar respuesta';
                this.procesando = false;
            }
        });
    }

    // Edición

    iniciarEdicionComentario(comentario: Comentario): void {
        this.editandoComentario[comentario.idComentario!] = true;
        this.contenidoEditado[comentario.idComentario!] = comentario.contenido;
    }

    guardarEdicionComentario(comentario: Comentario): void {
        const nuevoContenido = this.contenidoEditado[comentario.idComentario!];

        if (!nuevoContenido || !nuevoContenido.trim()) {
            this.error = 'El comentario no puede estar vacío';
            return;
        }

        this.procesando = true;
        this.error = '';

        this.comentarioService.actualizar(comentario.idComentario!, nuevoContenido.trim()).subscribe({
            next: (response) => {
                if (response.success) {
                    comentario.contenido = nuevoContenido.trim();
                    this.editandoComentario[comentario.idComentario!] = false;
                    this.exito = 'Comentario actualizado';
                    this.limpiarMensajes();
                } else {
                    this.error = response.message;
                }
                this.procesando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al actualizar comentario';
                this.procesando = false;
            }
        });
    }

    cancelarEdicionComentario(idComentario: number): void {
        this.editandoComentario[idComentario] = false;
    }

    iniciarEdicionRespuesta(respuesta: Respuesta): void {
        this.editandoRespuesta[respuesta.idRespuesta!] = true;
        this.contenidoEditado[respuesta.idRespuesta!] = respuesta.contenido;
    }

    guardarEdicionRespuesta(respuesta: Respuesta): void {
        const nuevoContenido = this.contenidoEditado[respuesta.idRespuesta!];

        if (!nuevoContenido || !nuevoContenido.trim()) {
            this.error = 'La respuesta no puede estar vacía';
            return;
        }

        this.procesando = true;
        this.error = '';

        this.respuestaService.actualizar(respuesta.idRespuesta!, nuevoContenido.trim()).subscribe({
            next: (response) => {
                if (response.success) {
                    respuesta.contenido = nuevoContenido.trim();
                    this.editandoRespuesta[respuesta.idRespuesta!] = false;
                    this.exito = 'Respuesta actualizada';
                    this.limpiarMensajes();
                } else {
                    this.error = response.message;
                }
                this.procesando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al actualizar respuesta';
                this.procesando = false;
            }
        });
    }

    cancelarEdicionRespuesta(idRespuesta: number): void {
        this.editandoRespuesta[idRespuesta] = false;
    }

    // Eliminación

    eliminarComentario(comentario: Comentario): void {
        if (!confirm('¿Estás seguro de eliminar este comentario?')) {
            return;
        }

        this.procesando = true;
        this.error = '';

        this.comentarioService.eliminar(comentario.idComentario!).subscribe({
            next: (response) => {
                if (response.success) {
                    this.cargarComentarios();
                    this.cargarPromedio();
                    this.exito = 'Comentario eliminado';
                    this.limpiarMensajes();
                } else {
                    this.error = response.message;
                }
                this.procesando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al eliminar comentario';
                this.procesando = false;
            }
        });
    }

    eliminarRespuesta(respuesta: Respuesta): void {
        if (!confirm('¿Estás seguro de eliminar esta respuesta?')) {
            return;
        }

        this.procesando = true;
        this.error = '';

        this.respuestaService.eliminar(respuesta.idRespuesta!).subscribe({
            next: (response) => {
                if (response.success) {
                    this.cargarComentarios(); // Recargar para actualizar contador
                    this.exito = 'Respuesta eliminada';
                    this.limpiarMensajes();
                } else {
                    this.error = response.message;
                }
                this.procesando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al eliminar respuesta';
                this.procesando = false;
            }
        });
    }

    // Utilidades

    esAutor(idUsuario: number): boolean {
        return this.usuarioActual && this.usuarioActual.idUsuario === idUsuario;
    }

    getEstrellas(calificacion: number): string[] {
        return Array(5).fill('☆').map((_, i) => i < calificacion ? '★' : '☆');
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