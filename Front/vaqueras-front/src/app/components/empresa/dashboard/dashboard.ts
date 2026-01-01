import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { JuegoService } from '../../../services/juego';
import { ComisionService } from '../../../services/comision';
import { AuthService } from '../../../services/auth';
import { UsuarioEmpresaService } from '../../../services/usuario-empresa';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './dashboard.html',
    styleUrl: './dashboard.css'
})
export class DashboardEmpresa implements OnInit {
    stats = { totalJuegos: 0, totalUsuarios: 0, comision: 0 };
    usuarioActual: any;

    constructor(
        private juegoService: JuegoService,
        private usuarioEmpresaService: UsuarioEmpresaService,
        private comisionService: ComisionService,
        private authService: AuthService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.usuarioActual = this.authService.getCurrentUser();
        this.juegoService.obtenerPorEmpresa(this.usuarioActual.idEmpresa).subscribe({
            next: (res) => { if (res.success && res.data) this.stats.totalJuegos = res.data.length; }
        });
        this.usuarioEmpresaService.listarPorEmpresa(this.usuarioActual.idEmpresa).subscribe({
            next: (res) => { if (res.success && res.data) this.stats.totalUsuarios = res.data.length; }
        });
        this.comisionService.obtenerComisionEmpresa(this.usuarioActual.idEmpresa).subscribe({
            next: (res) => { if (res.success && res.data) this.stats.totalJuegos = res.data.porcentajeEspecifico; }
        });
    }

    cerrarSesion() {
        this.authService.logout();
    }
    
    navegar(ruta: string): void {
        this.router.navigate([ruta]);
    }
}