import { Component, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { CompraService } from '../../../services/compra';
import { CarteraService } from '../../../services/cartera';
import { JuegoService } from '../../../services/juego';
import { Juego } from '../../../models/juego';
import { AuthService } from '../../../services/auth';

@Component({
    selector: 'app-comprar',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './comprar.html',
    styleUrl: './comprar.css'
})
export class Comprar implements OnInit {
    juego: Juego | null = null;
    saldoActual = 0;

    cargando = false;
    procesando = false;
    error = '';
    compraExitosa = false;

    usuarioActual: any;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private location: Location,
        private compraService: CompraService,
        private carteraService: CarteraService,
        private juegoService: JuegoService,
        private authService: AuthService
    ) { }

    ngOnInit(): void {
        this.usuarioActual = this.authService.getCurrentUser();

        // Obtener ID del juego de la URL
        const idJuego = this.route.snapshot.paramMap.get('id');

        if (idJuego) {
            this.cargarDatos(+idJuego);
        } else {
            this.error = 'ID de juego no válido';
        }
    }

    cargarDatos(idJuego: number): void {
        this.cargando = true;

        // Cargar juego y saldo en paralelo
        Promise.all([
            this.cargarJuego(idJuego),
            this.cargarSaldo()
        ]).then(() => {
            this.cargando = false;
        }).catch(() => {
            this.cargando = false;
        });
    }

    cargarJuego(idJuego: number): Promise<void> {
        return new Promise((resolve, reject) => {
            this.juegoService.obtenerPorId(idJuego).subscribe({
                next: (response) => {
                    if (response.success && response.data) {
                        this.juego = response.data;
                        resolve();
                    } else {
                        this.error = 'Juego no encontrado';
                        reject();
                    }
                },
                error: (err) => {
                    this.error = 'Error al cargar el juego';
                    console.error(err);
                    reject();
                }
            });
        });
    }

    cargarSaldo(): Promise<void> {
        return new Promise((resolve, reject) => {
            this.carteraService.obtenerCartera(this.usuarioActual.idUsuario).subscribe({
                next: (response) => {
                    if (response.success && response.data) {
                        this.saldoActual = response.data.saldo;
                        resolve();
                    } else {
                        // Si no tiene cartera, saldo = 0
                        this.saldoActual = 0;
                        resolve();
                    }
                },
                error: (err) => {
                    console.error('Error al cargar saldo:', err);
                    this.saldoActual = 0;
                    resolve(); // No bloqueamos la compra por esto
                }
            });
        });
    }

    confirmarCompra(): void {
        if (!this.juego) return;

        this.procesando = true;
        this.error = '';

        const compra = {
            idUsuario: this.usuarioActual.idUsuario,
            idJuego: this.juego.idJuego,
            fechaCompra: new Date().toISOString().split('T')[0]
        };

        this.compraService.comprar(compra).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    // Actualizar saldo local
                    this.saldoActual = response.data.nuevoSaldo;
                    this.compraExitosa = true;

                    // Redirigir después de 3 segundos
                    setTimeout(() => {
                        this.irABiblioteca();
                    }, 3000);
                } else {
                    this.error = response.message;
                }
                this.procesando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al procesar la compra';
                this.procesando = false;
                console.error('Error en compra:', err);
            }
        });
    }

    irABiblioteca(): void {
        this.router.navigate(['/jugador/biblioteca']);
    }

    volver(): void {
        this.location.back();
    }

    irACartera(): void {
        this.router.navigate(['/jugador/cartera']);
    }

    get tieneSaldoSuficiente(): boolean {
        if (!this.juego) return false;
        return this.saldoActual >= this.juego.precio;
    }

    get faltante(): number {
        if (!this.juego) return 0;
        return Math.max(0, this.juego.precio - this.saldoActual);
    }

    get clasificacionTexto(): string {
        if (!this.juego) return '';

        switch (this.juego.clasificacionEdad?.toUpperCase()) {
            case 'E':
                return 'E - Para todos';
            case 'T':
                return 'T - 12+ años';
            case 'M':
                return 'M - 16+ años';
            case 'AO':
                return 'AO - 18+ años (Solo adultos)';
            default:
                return this.juego.clasificacionEdad || 'Sin clasificación';
        }
    }
}