import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GrupoFamiliarService, GrupoFamiliar, MiembroGrupo, Invitacion } from '../../../services/grupo-familiar';
import { UsuarioService} from '../../../services/usuario';
import { Usuario } from '../../../models/usuario';
import { AuthService } from '../../../services/auth';

@Component({
    selector: 'app-grupos',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './grupos.html',
    styleUrl: './grupos.css'
})
export class GruposComponent implements OnInit {
    // Datos
    misGrupos: GrupoFamiliar[] = [];
    grupoSeleccionado: GrupoFamiliar | null = null;
    miembrosGrupo: MiembroGrupo[] = [];
    invitacionesPendientes: Invitacion[] = [];

    // Búsqueda de usuarios
    busquedaNickname = '';
    usuariosEncontrados: Usuario[] = [];
    usuarioSeleccionado: Usuario | null = null;
    buscando = false;

    // Formularios
    nombreNuevoGrupo = '';

    // Modales
    mostrarModalNuevoGrupo = false;
    mostrarModalInvitar = false;

    // Estados
    cargando = false;
    cargandoMiembros = false;
    error = '';
    exito = '';

    // Usuario actual
    usuarioActual: any;

    constructor(
        private grupoService: GrupoFamiliarService,
        private usuarioService: UsuarioService,
        private authService: AuthService
    ) { }

    ngOnInit(): void {
        this.usuarioActual = this.authService.getCurrentUser();
        this.cargarMisGrupos();
        this.cargarInvitaciones();
    }

    cargarMisGrupos(): void {
        this.cargando = true;
        this.grupoService.obtenerMisGrupos(this.usuarioActual.idUsuario).subscribe({
            next: (response) => {
                if (response.success) {
                    this.misGrupos = response.data || [];
                } else {
                    this.error = response.message;
                }
                this.cargando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al cargar grupos';
                this.cargando = false;
                console.error(err);
            }
        });
    }

    cargarInvitaciones(): void {
        this.grupoService.obtenerInvitacionesPendientes(this.usuarioActual.idUsuario).subscribe({
            next: (response) => {
                if (response.success) {
                    this.invitacionesPendientes = response.data || [];
                }
            },
            error: (err) => {
                console.error('Error al cargar invitaciones:', err);
            }
        });
    }


    abrirModalNuevoGrupo(): void {
        this.nombreNuevoGrupo = '';
        this.mostrarModalNuevoGrupo = true;
        this.error = '';
    }

    crearGrupo(): void {        
        if (!this.nombreNuevoGrupo.trim()) {
            this.error = 'El nombre del grupo es requerido';
            return;
        }

        this.cargando = true;        
        this.grupoService.crear({
            nombre: this.nombreNuevoGrupo,
            idCreador: this.usuarioActual.idUsuario
        }).subscribe({
            next: (response) => {
                if (response.success) {
                    this.exito = 'Grupo creado exitosamente';
                    this.mostrarModalNuevoGrupo = false;
                    this.cargarMisGrupos();
                    this.limpiarMensajes();
                } else {                
                    this.error = response.message;
                }
                this.cargando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al crear grupo';
                this.cargando = false;
                console.error(err);
            }
        });
    }

    seleccionarGrupo(grupo: GrupoFamiliar): void {
        this.grupoSeleccionado = grupo;
        this.cargarMiembrosGrupo(grupo.idGrupo!);
    }

    cargarMiembrosGrupo(idGrupo: number): void {
        this.cargandoMiembros = true;
        this.grupoService.obtenerMiembros(idGrupo).subscribe({
            next: (response) => {
                if (response.success) {
                    this.miembrosGrupo = response.data || [];
                }
                this.cargandoMiembros = false;
            },
            error: (err) => {
                console.error('Error al cargar miembros:', err);
                this.cargandoMiembros = false;
            }
        });
    }

    eliminarGrupo(): void {
        if (!this.grupoSeleccionado?.idGrupo) return;

        if (!confirm('¿Eliminar el grupo permanentemente?')) {
            return;
        }

        this.cargando = true;
        this.grupoService.eliminar(this.grupoSeleccionado.idGrupo).subscribe({
            next: (response) => {
                if (response.success) {
                    this.exito = 'Grupo eliminado exitosamente';
                    this.grupoSeleccionado = null;
                    this.cargarMisGrupos();
                    this.limpiarMensajes();
                } else {
                    this.error = response.message;
                }
                this.cargando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al eliminar grupo';
                this.cargando = false;
                console.error(err);
            }
        });
    }
    

    abrirModalInvitar(): void {
        if (!this.grupoSeleccionado) {
            this.error = 'Selecciona un grupo primero';
            return;
        }

        this.busquedaNickname = '';
        this.usuariosEncontrados = [];
        this.usuarioSeleccionado = null;
        this.mostrarModalInvitar = true;
        this.error = '';
    }

    buscarUsuarios(): void {
        // Validación UI: campo no vacío
        if (!this.busquedaNickname.trim()) {
            this.error = 'Ingresa un nickname para buscar';
            return;
        }

        if (this.busquedaNickname.trim().length < 3) {
            this.error = 'Ingresa al menos 3 caracteres';
            return;
        }

        this.buscando = true;
        this.error = '';

        this.usuarioService.buscarPorNickname(this.busquedaNickname).subscribe({
            next: (response) => {
                if (response.success) {
                    this.usuariosEncontrados = response.data || [];
                    if (this.usuariosEncontrados.length === 0) {
                        this.error = 'No se encontraron usuarios con ese nickname';
                    }
                } else {
                    this.error = response.message;
                }
                this.buscando = false;
            },
            error: (err) => {
                this.error = 'Error al buscar usuarios';
                this.buscando = false;
                console.error(err);
            }
        });
    }

    seleccionarUsuario(usuario: Usuario): void {
        this.usuarioSeleccionado = usuario;
        this.error = '';
    }

    enviarInvitacion(): void {
        if (!this.usuarioSeleccionado) {
            this.error = 'Selecciona un usuario de la lista';
            return;
        }

        if (!this.grupoSeleccionado?.idGrupo) return;

        this.cargando = true;
        
        const invitacionData = {
            idGrupo: this.grupoSeleccionado.idGrupo,
            idUsuarioInvitado: this.usuarioSeleccionado.idUsuario,
            idUsuarioInvitador: this.usuarioActual.idUsuario,
            fechaInvitacion: new Date().toISOString().split('T')[0]  // YYYY-MM-DD
        };

        this.grupoService.enviarInvitacion(invitacionData).subscribe({
            next: (response) => {
                if (response.success) {
                    this.exito = `Invitación enviada a ${this.usuarioSeleccionado!.nickname}`;
                    this.mostrarModalInvitar = false;
                    this.limpiarMensajes();
                } else {
                    
                    this.error = response.message;
                }
                this.cargando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al enviar invitación';
                this.cargando = false;
                console.error(err);
            }
        });
    }

    aceptarInvitacion(invitacion: Invitacion): void {
        if (!invitacion.idInvitacion) return;

        this.cargando = true;
        this.grupoService.aceptarInvitacion(invitacion.idInvitacion).subscribe({
            next: (response) => {
                if (response.success) {
                    this.exito = 'Invitación aceptada. Ahora eres miembro del grupo';
                    this.cargarInvitaciones();
                    this.cargarMisGrupos();
                    this.limpiarMensajes();
                } else {
                    this.error = response.message;
                }
                this.cargando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al aceptar invitación';
                this.cargando = false;
                console.error(err);
            }
        });
    }

    rechazarInvitacion(invitacion: Invitacion): void {
        if (!invitacion.idInvitacion) return;

        this.cargando = true;
        this.grupoService.rechazarInvitacion(invitacion.idInvitacion).subscribe({
            next: (response) => {
                if (response.success) {
                    this.exito = 'Invitación rechazada';
                    this.cargarInvitaciones();
                    this.limpiarMensajes();
                } else {
                    this.error = response.message;
                }
                this.cargando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al rechazar invitación';
                this.cargando = false;
                console.error(err);
            }
        });
    }

    eliminarMiembro(miembro: MiembroGrupo): void {
        if (!this.grupoSeleccionado?.idGrupo) return;

        if (!confirm(`¿Eliminar a ${miembro.nickname} del grupo?`)) {
            return;
        }

        this.cargando = true;       
        this.grupoService.eliminarMiembro(
            this.grupoSeleccionado.idGrupo,
            miembro.idUsuario
        ).subscribe({
            next: (response) => {
                if (response.success) {
                    this.exito = 'Miembro eliminado exitosamente';
                    this.cargarMiembrosGrupo(this.grupoSeleccionado!.idGrupo!);
                    this.limpiarMensajes();
                } else {
                    this.error = response.message;
                }
                this.cargando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al eliminar miembro';
                this.cargando = false;
                console.error(err);
            }
        });
    }

    salirDelGrupo(): void {
        if (!this.grupoSeleccionado?.idGrupo) return;

        if (!confirm('¿Estás seguro de salir del grupo?')) {
            return;
        }

        this.cargando = true;        
        this.grupoService.eliminarMiembro(
            this.grupoSeleccionado.idGrupo,
            this.usuarioActual.idUsuario
        ).subscribe({
            next: (response) => {
                if (response.success) {
                    this.exito = 'Has salido del grupo';
                    this.grupoSeleccionado = null;
                    this.cargarMisGrupos();
                    this.limpiarMensajes();
                } else {
                    this.error = response.message;
                }
                this.cargando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al salir del grupo';
                this.cargando = false;
                console.error(err);
            }
        });
    }

    esCreador(grupo: GrupoFamiliar): boolean {
        return grupo.idCreador === this.usuarioActual?.idUsuario;
    }

    esMiCreador(): boolean {
        if (!this.grupoSeleccionado) return false;
        return this.grupoSeleccionado.idCreador === this.usuarioActual?.idUsuario;
    }

    cancelarModal(): void {
        this.mostrarModalNuevoGrupo = false;
        this.mostrarModalInvitar = false;
        this.error = '';
        this.usuariosEncontrados = [];
        this.usuarioSeleccionado = null;
    }

    limpiarMensajes(): void {
        setTimeout(() => {
            this.error = '';
            this.exito = '';
        }, 3000);
    }
}