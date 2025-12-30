import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ComentarioService } from '../../services/cometario';
import { AuthService } from '../../services/auth';
import { Comentario, Respuesta, CreateComentarioRequest, CreateRespuestaRequest } from '../../models/comentarios_respuestas';
import { from } from 'rxjs';

@Component({
    selector: 'app-comentarios',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './comentario.html',
    styleUrls: ['./comentario.css']
})
export class ComentariosComponent implements OnInit {
    @Input() idJuego!: number;
    @Input() tituloJuego!: string;

    comentarios: Comentario[] = [];
    comentarioForm: FormGroup;
    respuestasForms: Map<number, FormGroup> = new Map();

    loading = false;
    errorMessage = '';
    successMessage = '';

    // Control de qué comentarios muestran el formulario de respuesta
    mostrarRespuestaForm: Set<number> = new Set();

    // Control de licencia del usuario
    usuarioTieneLicencia = false;
    verificandoLicencia = false;

    constructor(
        private fb: FormBuilder,
        private comentarioService: ComentarioService,
        private authService: AuthService
    ) {
        this.comentarioForm = this.fb.group({
            contenido: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]]
        });
    }

    ngOnInit(): void {
        this.verificarLicenciaUsuario();
        this.cargarComentarios();
    }

   //Verifica si el usuario tiene licencia de un juego para comentar
    verificarLicenciaUsuario(): void {
        const usuario = this.authService.getCurrentUser();
        if (!usuario) {
            this.usuarioTieneLicencia = false;
            return;
        }

        this.verificandoLicencia = true;
        this.comentarioService.puedeResponder(usuario.idUsuario, this.idJuego).subscribe({
            next: (tiene) => {
                this.usuarioTieneLicencia = tiene;
                this.verificandoLicencia = false;
                if (!tiene) {
                    console.log('Usuario no tiene licencia del juego');
                }
            },
            error: (error) => {
                console.error('Error al verificar licencia:', error);
                this.usuarioTieneLicencia = false;
                this.verificandoLicencia = false;
            }
        });
    }

    //Carga los comentarios del juego
    cargarComentarios(): void {
        this.loading = true;
        this.comentarioService.getComentariosPorJuego(this.idJuego).subscribe({
            next: (comentarios) => {
                this.comentarios = comentarios;
                // Crear formularios de respuesta para cada comentario
                comentarios.forEach(comentario => {
                    this.respuestasForms.set(
                        comentario.idComentario,
                        this.fb.group({
                            contenido: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]]
                        })
                    );
                });
                this.loading = false;
            },
            error: (error) => {
                console.error('Error al cargar comentarios:', error);
                this.errorMessage = 'Error al cargar los comentarios';
                this.loading = false;
            }
        });
    }

    //Crea un comentario nuevo
    crearComentario(): void {
        if (this.comentarioForm.invalid) {
            this.comentarioForm.markAllAsTouched();
            return;
        }

        if (!this.usuarioTieneLicencia) {
            this.errorMessage = 'Debes tener licencia del juego para poder comentar';
            return;
        }

        const usuario = this.authService.getCurrentUser();
        if (!usuario) {
            this.errorMessage = 'Debes iniciar sesión para comentar';
            return;
        }

        const request: CreateComentarioRequest = {
            idUsuario: usuario.idUsuario,
            idJuego: this.idJuego,
            contenido: this.comentarioForm.value.contenido.trim()
        };

        this.loading = true;
        this.errorMessage = '';
        this.successMessage = '';

        this.comentarioService.createComentario(request).subscribe({
            next: (response) => {
                this.loading = false;
                if (response.success) {
                    this.successMessage = 'Comentario agregado exitosamente';
                    this.comentarioForm.reset();
                    this.cargarComentarios(); // Recargar comentarios
                    setTimeout(() => this.successMessage = '', 3000);
                } else {
                    this.errorMessage = response.message;
                }
            },
            error: (error) => {
                console.error('Error al crear comentario:', error);
                this.errorMessage = error.error?.message || 'Error al crear el comentario';
                this.loading = false;
            }
        });
    }

    //Crea respuesta a un comentario
    crearRespuesta(idComentario: number): void {
        const respuestaForm = this.respuestasForms.get(idComentario);
        if (!respuestaForm || respuestaForm.invalid) {
            respuestaForm?.markAllAsTouched();
            return;
        }

        if (!this.usuarioTieneLicencia) {
            this.errorMessage = 'Solo los usuarios con licencia del juego pueden responder comentarios';
            setTimeout(() => this.errorMessage = '', 5000);
            return;
        }

        const usuario = this.authService.getCurrentUser();
        if (!usuario) {
            this.errorMessage = 'Debes iniciar sesión para responder';
            return;
        }

        const request: CreateRespuestaRequest = {
            idComentario,
            idUsuario: usuario.idUsuario,
            contenido: respuestaForm.value.contenido.trim()
        };

        this.loading = true;
        this.errorMessage = '';
        
        this.comentarioService.createRespuesta(request, this.idJuego).subscribe({
            next: (response) => {
                this.loading = false;
                if (response.success) {
                    this.successMessage = 'Respuesta agregada exitosamente';
                    respuestaForm.reset();
                    this.mostrarRespuestaForm.delete(idComentario);
                    this.cargarComentarios(); // Recargar comentarios
                    setTimeout(() => this.successMessage = '', 3000);
                } else {
                    this.errorMessage = response.message;
                    setTimeout(() => this.errorMessage = '', 5000);
                }
            },
            error: (error) => {
                console.error('Error al crear respuesta:', error);
                this.errorMessage = error.error?.message || 'Error al crear la respuesta';
                this.loading = false;
                setTimeout(() => this.errorMessage = '', 5000);
            }
        });
    }

    //avisa que solo usuario con licencia pueden responder
    toggleRespuestaForm(idComentario: number): void {
        if (this.mostrarRespuestaForm.has(idComentario)) {
            this.mostrarRespuestaForm.delete(idComentario);
        } else {
            if (!this.usuarioTieneLicencia) {
                this.errorMessage = 'Solo los usuarios con licencia del juego pueden responder comentarios';
                setTimeout(() => this.errorMessage = '', 5000);
                return;
            }
            this.mostrarRespuestaForm.add(idComentario);
        }
    }

    //RESPUESTAS
    getRespuestaForm(idComentario: number): FormGroup {
        return this.respuestasForms.get(idComentario)!;
    }
    
    deberMostrarRespuestaForm(idComentario: number): boolean {
        return this.mostrarRespuestaForm.has(idComentario);
    }

    //le da formato a la fecha
    formatearFecha(fecha: string): string {
        return new Date(fecha).toLocaleDateString('es-ES', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }
}


