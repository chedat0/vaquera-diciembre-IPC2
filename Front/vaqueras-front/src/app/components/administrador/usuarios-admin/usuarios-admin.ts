import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioAdminService, UsuarioAdmin } from '../../../services/usuario-admin';

@Component({
    selector: 'app-gestion-administradores',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './usuarios-admin.html',
    styleUrl: './usuarios-admin.css'
})

export class GestionAdministradores implements OnInit {
    administradores: UsuarioAdmin[] = [];
    adminActual: UsuarioAdmin = this.getAdminVacio();
    adminPassword: UsuarioAdmin | null = null;

    nuevaPassword = '';
    confirmarPassword = '';

    mostrarModalForm = false;
    mostrarModalPassword = false;
    modoEdicion = false;
    cargando = false;
    procesando = false;
    error = '';
    exito = '';

    constructor(private adminService: UsuarioAdminService) { }

    ngOnInit(): void {
        this.cargar();
    }

    cargar(): void {
        this.cargando = true;
        this.adminService.listarTodos().subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    this.administradores = res.data;
                }
                this.cargando = false;
            },
            error: (err) => {
                this.error = 'Error al cargar administradores';
                this.cargando = false;
            }
        });
    }

    abrirModalCrear(): void {
        this.modoEdicion = false;
        this.adminActual = this.getAdminVacio();
        this.mostrarModalForm = true;
    }

    abrirModalEditar(admin: UsuarioAdmin): void {
        this.modoEdicion = true;
        this.adminActual = { ...admin };
        this.mostrarModalForm = true;
    }

    cerrarModalForm(): void {
        this.mostrarModalForm = false;
        this.adminActual = this.getAdminVacio();
        this.error = '';
    }

    abrirModalPassword(admin: UsuarioAdmin): void {
        this.adminPassword = admin;
        this.nuevaPassword = '';
        this.confirmarPassword = '';
        this.mostrarModalPassword = true;
    }

    cerrarModalPassword(): void {
        this.mostrarModalPassword = false;
        this.adminPassword = null;
        this.nuevaPassword = '';
        this.confirmarPassword = '';
        this.error = '';
    }

    guardar(): void {        
        this.procesando = true;
        this.error = '';

        if (this.modoEdicion && this.adminActual.idUsuario) {           
            this.adminService.actualizar(this.adminActual.idUsuario, this.adminActual).subscribe({
                next: (res) => {
                    if (res.success) {
                        this.exito = 'Administrador actualizado exitosamente';
                        this.cerrarModalForm();
                        this.cargar();
                        setTimeout(() => { this.exito = ''; }, 3000);
                    } else {
                        this.error = res.message;
                    }
                    this.procesando = false;
                },
                error: (err) => {
                    this.error = err.error?.message || 'Error al actualizar';
                    this.procesando = false;
                }
            });
        } else {
            
            this.adminService.crear(this.adminActual).subscribe({
                next: (res) => {
                    if (res.success) {
                        this.exito = 'Administrador creado exitosamente';
                        this.cerrarModalForm();
                        this.cargar();
                        setTimeout(() => { this.exito = ''; }, 3000);
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

    cambiarPassword(): void {        
        if (!this.adminPassword?.idUsuario) return;

        this.procesando = true;
        this.error = '';

        this.adminService.cambiarPassword(this.adminPassword.idUsuario, this.nuevaPassword).subscribe({
            next: (res) => {
                if (res.success) {
                    this.exito = 'Contraseña actualizada exitosamente';
                    this.cerrarModalPassword();
                    setTimeout(() => { this.exito = ''; }, 3000);
                } else {
                    this.error = res.message;
                }
                this.procesando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al cambiar contraseña';
                this.procesando = false;
            }
        });
    }

    eliminar(admin: UsuarioAdmin): void {
        const confirmacion = confirm(
            `⚠️ ADVERTENCIA\n\n` +
            `¿Estás seguro de eliminar al administrador "${admin.nickname}"?\n\n` +
            `Esta acción no se puede deshacer.`
        );

        if (!confirmacion) return;

        this.adminService.eliminar(admin.idUsuario!).subscribe({
            next: (res) => {
                if (res.success) {
                    this.exito = 'Administrador eliminado exitosamente';
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

    private getAdminVacio(): UsuarioAdmin {
        return {
            nickname: '',
            correo: '',
            password: '',
            fechaNacimiento: '',
            telefono: '',
            pais: ''
        };
    }
}