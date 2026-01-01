import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BannerService } from '../../../services/banner';
import { Banner } from '../../../models/banner';
import { JuegoService } from '../../../services/juego';
import { Juego } from '../../../models/juego';
import { ImagenService } from '../../../services/imagen';

@Component({
    selector: 'app-gestion-banners',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './banner.html',
    styleUrl: './banner.css'
})

export class GestionBanners implements OnInit {
    banners: Banner[] = [];
    bannersActivos: Banner[] = [];
    juegos: Juego[] = [];
    juegosSugeridos: any[] = [];

    bannerActual: Banner = this.getBannerVacio();
    bannerImagen: Banner | null = null;
    imagenSeleccionada: { file: File; preview: string } | null = null;

    vistaActual: 'todos' | 'activos' | 'sugerencias' = 'todos';
    mostrarModalForm = false;
    mostrarModalImagen = false;
    modoEdicion = false;
    cargando = false;
    procesando = false;
    error = '';
    exito = '';

    constructor(
        private bannerService: BannerService,
        private juegoService: JuegoService,
        private imagenService: ImagenService
    ) { }

    ngOnInit(): void {
        this.cargar();
        this.cargarJuegos();
    }

    cargar(): void {
        this.cargando = true;
        this.bannerService.obtenerTodos().subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    this.banners = res.data.sort((a, b) => a.ordenPrioridad - b.ordenPrioridad);
                }
                this.cargando = false;
            },
            error: () => { this.cargando = false; }
        });
    }

    cargarActivos(): void {
        this.cargando = true;
        this.bannerService.obtenerActivos().subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    this.bannersActivos = res.data.sort((a, b) => a.ordenPrioridad - b.ordenPrioridad);
                }
                this.cargando = false;
            },
            error: () => { this.cargando = false; }
        });
    }

    cargarSugerencias(): void {
        this.cargando = true;
        this.bannerService.obtenerMejorBalance(10).subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    this.juegosSugeridos = res.data;
                }
                this.cargando = false;
            },
            error: () => { this.cargando = false; }
        });
    }

    cargarJuegos(): void {
        this.juegoService.obtenerTodos().subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    this.juegos = res.data;
                }
            }
        });
    }

    abrirModalCrear(): void {
        this.modoEdicion = false;
        this.bannerActual = this.getBannerVacio();
        this.mostrarModalForm = true;
    }

    abrirModalEditar(banner: Banner): void {
        this.modoEdicion = true;
        this.bannerActual = { ...banner };
        this.mostrarModalForm = true;
    }

    cerrarModalForm(): void {
        this.mostrarModalForm = false;
        this.error = '';
    }

    crearBannerDesdeJuego(juego: any): void {
        this.bannerActual = {
            idJuego: juego.idJuego,
            ordenPrioridad: this.banners.length + 1,
            activo: true
        };
        this.modoEdicion = false;
        this.mostrarModalForm = true;
    }

    guardar(): void {        
        this.procesando = true;
        this.error = '';

        if (this.modoEdicion && this.bannerActual.idBanner) {
            // Actualizar posición y fechas
            this.bannerService.actualizarPosicion(this.bannerActual.idBanner, this.bannerActual.ordenPrioridad).subscribe({
                next: (res) => {
                    if (res.success) {
                        // Si hay fechas, actualizarlas también
                        if (this.bannerActual.fechaCreacion || this.bannerActual.fechaFin) {
                            this.actualizarFechas();
                        } else {
                            this.finalizarGuardado();
                        }
                    } else {
                        this.error = res.message;
                        this.procesando = false;
                    }
                },
                error: (err) => {
                    this.error = err.error?.message || 'Error al actualizar';
                    this.procesando = false;
                }
            });
        } else {
            // Crear nuevo
            this.bannerService.crear(this.bannerActual).subscribe({
                next: (res) => {
                    if (res.success && res.data) {
                        this.exito = 'Banner creado. Ahora sube la imagen.';
                        this.cerrarModalForm();
                        this.cargar();
                        
                        setTimeout(() => {
                            this.abrirModalImagen(res.data!);
                        }, 500);
                    } else {
                        this.error = res.message;
                    }
                    this.procesando = false;
                },
                error: (err) => {
                    this.error = err.error?.message || 'Error al crear';
                    this.procesando = false;
                }
            });
        }
    }

    private actualizarFechas(): void {
        this.bannerService.actualizarFechas(
            this.bannerActual.idBanner!,
            this.bannerActual.fechaCreacion || '',
            this.bannerActual.fechaFin || ''
        ).subscribe({
            next: () => {
                this.finalizarGuardado();
            },
            error: (err) => {
                this.error = 'Error al actualizar fechas';
                this.procesando = false;
            }
        });
    }

    private finalizarGuardado(): void {
        this.exito = 'Banner actualizado exitosamente';
        this.cerrarModalForm();
        this.cargar();
        this.procesando = false;
        setTimeout(() => { this.exito = ''; }, 3000);
    }

    toggleEstado(banner: Banner): void {
        const nuevoEstado = !banner.activo;

        this.bannerService.actualizarEstado(banner.idBanner!, nuevoEstado).subscribe({
            next: (res) => {
                if (res.success) {
                    this.exito = `Banner ${nuevoEstado ? 'activado' : 'desactivado'}`;
                    this.cargar();
                    setTimeout(() => { this.exito = ''; }, 3000);
                }
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al cambiar estado';
            }
        });
    }

    eliminar(banner: Banner): void {
        if (!confirm(`¿Eliminar banner de "${banner.tituloJuego}"?\n\nEsta acción no se puede deshacer.`)) return;

        this.bannerService.eliminar(banner.idBanner!).subscribe({
            next: (res) => {
                if (res.success) {
                    this.exito = 'Banner eliminado';
                    this.cargar();
                    setTimeout(() => { this.exito = ''; }, 3000);
                }
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al eliminar';
            }
        });
    }

    abrirModalImagen(banner: Banner): void {
        this.bannerImagen = banner;
        this.imagenSeleccionada = null;
        this.mostrarModalImagen = true;
    }

    cerrarModalImagen(): void {
        this.mostrarModalImagen = false;
        this.bannerImagen = null;
        this.imagenSeleccionada = null;
    }

    seleccionarImagen(event: any): void {
        const file: File = event.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = (e: any) => {
            this.imagenSeleccionada = {
                file: file,
                preview: e.target.result
            };
        };
        reader.readAsDataURL(file);
    }

    subirImagen(): void {
        if (!this.imagenSeleccionada || !this.bannerImagen) return;

        this.procesando = true;

        this.imagenService.subirImagenBanner(this.bannerImagen.idBanner!, this.imagenSeleccionada.file).subscribe({
            next: (res) => {
                if (res.success) {
                    this.exito = 'Imagen subida exitosamente';
                    this.cerrarModalImagen();
                    this.cargar();
                    setTimeout(() => { this.exito = ''; }, 3000);
                }
                this.procesando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al subir imagen';
                this.procesando = false;
            }
        });
    }

    getUrlImagen(idBanner: number): string {
        return this.imagenService.getUrlBanner(idBanner);
    }

    onImageError(event: any): void {
        event.target.src = 'assets/placeholder-banner.png';
    }

    private getBannerVacio(): Banner {
        return {
            idJuego: 0,
            ordenPrioridad: 1,
            activo: true
        };
    }
}