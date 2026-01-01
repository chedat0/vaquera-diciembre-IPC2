import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioEmpresaService, UsuarioEmpresa } from '../../../services/usuario-empresa';
import { AuthService } from '../../../services/auth';

@Component({
    selector: 'app-usuarios-empresa',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './usuarios-empresa.html',
    styleUrl: './usuarios-empresa.css'
})
export class UsuariosEmpresaComponent implements OnInit {
    usuarios: UsuarioEmpresa[] = [];
    nuevoUsuario: UsuarioEmpresa = { correo: '', nombre: '', fechaNacimiento: '', password: '', idEmpresa: 0 };

    mostrarFormulario = false;
    cargando = false;
    procesando = false;
    error = '';
    exito = '';

    usuarioActual: any;

    constructor(
        private usuarioEmpresaService: UsuarioEmpresaService,
        private authService: AuthService
    ) { }

    ngOnInit(): void {
        this.usuarioActual = this.authService.getCurrentUser();
        this.nuevoUsuario.idEmpresa = this.usuarioActual.idEmpresa;
        this.cargar();
    }

    cargar(): void {
        this.cargando = true;
        this.usuarioEmpresaService.listarPorEmpresa(this.usuarioActual.idEmpresa).subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    this.usuarios = res.data;
                }
                this.cargando = false;
            },
            error: (err) => {
                this.error = 'Error al cargar usuarios';
                this.cargando = false;
            }
        });
    }

    abrirFormulario(): void {
        this.mostrarFormulario = true;
        this.nuevoUsuario = { correo: '', nombre: '', fechaNacimiento: '', password: '', idEmpresa: this.usuarioActual.idEmpresa };
    }

    cerrarFormulario(): void {
        this.mostrarFormulario = false;
        this.error = '';
    }

    guardar(): void {        
        if (!this.nuevoUsuario.nombre.trim()) {
            this.error = 'El nombre es obligatorio';
            return;
        }

        if (!this.nuevoUsuario.correo.trim()) {
            this.error = 'El correo es obligatorio';
            return;
        }

        if (!this.nuevoUsuario.fechaNacimiento) {
            this.error = 'La fecha de nacimiento es obligatoria';
            return;
        }

        if (!this.nuevoUsuario.password) {
            this.error = 'La contraseña es obligatoria';
            return;
        }

        this.procesando = true;
        
        this.usuarioEmpresaService.crear(this.nuevoUsuario).subscribe({
            next: (res) => {
                if (res.success) {
                    this.exito = 'Usuario creado exitosamente';
                    this.cerrarFormulario();
                    this.cargar();
                    setTimeout(() => { this.exito = ''; }, 3000);
                } else {
                    this.error = res.message;
                }
                this.procesando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al crear usuario';
                this.procesando = false;
            }
        });
    }

    eliminar(usuario: UsuarioEmpresa): void {
        if (!confirm(`¿Eliminar a ${usuario.nombre}?`)) return;
    
        this.usuarioEmpresaService.eliminar(usuario.idUsuario!, this.usuarioActual.idEmpresa).subscribe({
            next: (res) => {
                if (res.success) {
                    this.exito = 'Usuario eliminado';
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