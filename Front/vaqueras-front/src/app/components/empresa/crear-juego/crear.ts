import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { JuegoService } from '../../../services/juego';
import { CreateJuegoRequest } from '../../../models/juego';
import { CategoriaService } from '../../../services/categoria';
import { ImagenService } from '../../../services/imagen';
import { AuthService } from '../../../services/auth';

@Component({
    selector: 'app-crear',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './crear.html',
    styleUrl: './crear.css'

})
export class CrearJuego implements OnInit {
    juego: CreateJuegoRequest = {
        titulo: '',
        descripcion: '',
        precio: 0,
        clasificacion: 'E',
        categorias: [],
        idEmpresa: 0,
        fechaCreacion: '',
        comentariosActivos: true
    };

    categorias: any[] = [];
    categoriaSeleccionada = '';
    imagenesSeleccionadas: { file: File; preview: string }[] = [];

    procesando = false;
    error = '';
    exito = '';

    usuarioActual: any;

    constructor(
        private juegoService: JuegoService,
        private categoriaService: CategoriaService,
        private imagenService: ImagenService,
        private authService: AuthService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.usuarioActual = this.authService.getCurrentUser();
        this.juego.idEmpresa = this.usuarioActual.idEmpresa;
        this.juego.fechaCreacion = new Date().toISOString().split('T')[0];
        this.cargarCategorias();
    }

    cargarCategorias(): void {
        this.categoriaService.obtenerActivas().subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    this.categorias = res.data;
                }
            },
            error: (err) => console.error(err)
        });
    }

    seleccionarImagenes(event: any): void {
        const files: FileList = event.target.files;

        for (let i = 0; i < files.length; i++) {
            const file = files[i];
            
            if (!file) continue;

            const reader = new FileReader();
            reader.onload = (e: any) => {
                this.imagenesSeleccionadas.push({
                    file: file,
                    preview: e.target.result
                });
            };
            reader.readAsDataURL(file);
        }
    }

    eliminarImagen(index: number): void {
        this.imagenesSeleccionadas.splice(index, 1);
    }

    guardar(): void {
        if (!this.juego.titulo.trim()) {
            this.error = 'El título es obligatorio';
            return;
        }

        if (!this.juego.descripcion.trim()) {
            this.error = 'La descripción es obligatoria';
            return;
        }

        if (!this.categoriaSeleccionada) {
            this.error = 'Debe seleccionar una categoría';
            return;
            
        }

        this.juego.categorias = [this.categoriaSeleccionada];

        this.procesando = true;
        this.error = '';

        this.juegoService.crear(this.juego).subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    const idJuego = res.data.idJuego!;

                    if (this.imagenesSeleccionadas.length > 0) {
                        this.subirImagenes(idJuego);
                    } else {
                        this.exito = 'Juego creado exitosamente';
                        setTimeout(() => this.router.navigate(['/empresa/juegos']), 2000);
                    }
                } else {
                    this.error = res.message;
                    this.procesando = false;
                }
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al crear juego';
                this.procesando = false;
            }
        });
    }

    private subirImagenes(idJuego: number): void {
        let contador = 0;
        const total = this.imagenesSeleccionadas.length;

        this.imagenesSeleccionadas.forEach((img, index) => {
            const esPortada = index === 0;

            // Backend validará: tipo, tamaño, etc.
            this.imagenService.subirImagenJuego(idJuego, img.file, esPortada).subscribe({
                next: () => {
                    contador++;
                    if (contador === total) {
                        this.exito = 'Juego e imágenes creados exitosamente';
                        setTimeout(() => this.router.navigate(['/empresa/juegos']), 2000);
                    }
                },
                error: (err) => {
                    console.error('Error al subir imagen:', err);
                    contador++;
                    if (contador === total) {
                        this.exito = 'Juego creado, pero algunas imágenes no se subieron';
                        setTimeout(() => this.router.navigate(['/empresa/juegos']), 2000);
                    }
                }
            });
        });
    }

    cancelar(): void {
        this.router.navigate(['/empresa/juegos']);
    }
}