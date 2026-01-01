import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { InstalacionService } from '../../../services/instalacion-compra';
import { CarteraService } from '../../../services/cartera';
import { AuthService } from '../../../services/auth';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './dashboard.html',
    styleUrl: './dashboard.css'
})
export class DashboardJugador implements OnInit {
    stats = { juegosDisponibles: 0, juegosInstalados: 0, saldoCartera: 0, juegoPrestado: false };

    usuarioActual: any;
    cargando = true;

    constructor(
        private instalacionService: InstalacionService,
        private carteraService: CarteraService,
        private authService: AuthService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.usuarioActual = this.authService.getCurrentUser();
        this.cargarStats();
    }

    cargarStats(): void {
        this.cargando = true;

        Promise.all([
            this.cargarJuegosDisponibles(),
            this.cargarJuegosInstalados(),
            this.cargarSaldo()
        ]).then(() => {
            this.cargando = false;
        }).catch(() => {
            this.cargando = false;
        });
    }

    private cargarJuegosDisponibles(): Promise<void> {
        return new Promise((resolve) => {
            this.instalacionService.obtenerDisponibles(this.usuarioActual.idUsuario).subscribe({
                next: (res) => {
                    if (res.success && res.data) {
                        this.stats.juegosDisponibles = res.data.length;
                    }
                    resolve();
                },
                error: () => resolve()
            });
        });
    }

    private cargarJuegosInstalados(): Promise<void> {
        return new Promise((resolve) => {
            this.instalacionService.obtenerInstalados(this.usuarioActual.idUsuario).subscribe({
                next: (res) => {
                    if (res.success && res.data) {
                        this.stats.juegosInstalados = res.data.length;
                        // Verificar si tiene juego prestado instalado
                        this.stats.juegoPrestado = res.data.some((j: any) => !j.esPropio);
                    }
                    resolve();
                },
                error: () => resolve()
            });
        });
    }

    private cargarSaldo(): Promise<void> {
        return new Promise((resolve) => {
            this.carteraService.obtenerCartera(this.usuarioActual.idUsuario).subscribe({
                next: (res) => {
                    if (res.success && res.data) {
                        this.stats.saldoCartera = res.data.saldo;
                    }
                    resolve();
                },
                error: () => resolve()
            });
        });
    }

    navegar(ruta: string): void {
        this.router.navigate([ruta]);
    }
}