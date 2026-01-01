import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PerfilJugadorService } from '../../../services/perfil-jugador';

@Component({
    selector: 'app-banear-jugadores',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './banear.html',
    styleUrl: './banear.css'
})
export class BanearJugadores implements OnInit {
    jugadores: any[] = [];
    busqueda = '';
    cargando = false;
    error = '';
    exito = '';

    constructor(private perfilService: PerfilJugadorService) { }

    ngOnInit(): void {
        this.listarTodos();
    }

    listarTodos(): void {
        this.cargando = true;
        this.perfilService.listarTodos().subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    this.jugadores = res.data;
                }
                this.cargando = false;
            },
            error: () => { this.cargando = false; }
        });
    }

    buscar(): void {
        if (!this.busqueda.trim()) {
            this.listarTodos();
            return;
        }

        this.cargando = true;
        this.perfilService.buscarJugadores(this.busqueda).subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    this.jugadores = res.data;
                }
                this.cargando = false;
            },
            error: () => { this.cargando = false; }
        });
    }

    banear(jugador: any): void {
        const confirmacion = confirm(
            `ADVERTENCIA: Está a punto de ELIMINAR permanentemente la cuenta de ${jugador.nickname}.\n\n` +
            `Esto eliminará:\n` +
            `- Su cuenta y todos sus datos\n` +
            `- Su biblioteca de juegos\n` +
            `- Sus comentarios y valoraciones\n` +
            `- Su historial de compras\n\n` +
            `Esta acción NO SE PUEDE DESHACER.\n\n` +
            `¿Está COMPLETAMENTE seguro?`
        );

        if (!confirmacion) return;

        this.perfilService.eliminarCuenta(jugador.idUsuario).subscribe({
            next: (res) => {
                if (res.success) {
                    this.exito = `La cuenta de ${jugador.nickname} ha sido eliminada permanentemente`;
                    this.buscar(); // Recargar lista
                    setTimeout(() => { this.exito = ''; }, 5000);
                } else {
                    this.error = res.message;
                }
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al eliminar cuenta';
            }
        });
    }
}