import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { JuegoService } from '../../../services/juego';
import { CategoriaService } from '../../../services/categoria';
import { ComisionService } from '../../../services/comision';
import { AuthService } from '../../../services/auth';

@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class DashboardAdmin implements OnInit {
  
  estadisticas = {
    totalJuegos: 0,
    totalCategorias: 0,
    comisionGlobal: 0,
    empresasConComision: 0
  };

  accionesRapidas = [
    { titulo: 'Gestionar Categorías', icono: '📁', ruta: '/admin/categorias', color: '#2196f3' },
    { titulo: 'Gestionar Comisiones', icono: '💰', ruta: '/admin/comisiones', color: '#4caf50' },
    { titulo: 'Ver Reportes', icono: '📊', ruta: '/admin/reportes', color: '#ff9800' },
    { titulo: 'Gestionar Juegos', icono: '🎮', ruta: '/admin/juegos', color: '#9c27b0' },
    { titulo: 'Cerrar Sesión', ruta: '/login', color: '#9c27b0' }
  ];

  cargando = true;

  constructor(
    private juegoService: JuegoService,
    private categoriaService: CategoriaService,
    private comisionService: ComisionService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarEstadisticas();
  }

  cargarEstadisticas(): void {
    this.cargando = true;

    // Cargar estadísticas en paralelo
    Promise.all([
      this.cargarJuegos(),
      this.cargarCategorias(),
      this.cargarComisionGlobal(),
      this.cargarEmpresasConComision()
    ]).then(() => {
      this.cargando = false;
    }).catch(err => {
      console.error('Error al cargar estadísticas:', err);
      this.cargando = false;
    });
  }

  private cargarJuegos(): Promise<void> {
    return new Promise((resolve) => {
      this.juegoService.obtenerTodos().subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.estadisticas.totalJuegos = response.data.length;
          }
          resolve();
        },
        error: () => resolve()
      });
    });
  }

  private cargarCategorias(): Promise<void> {
    return new Promise((resolve) => {
      this.categoriaService.obtenerTodas().subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.estadisticas.totalCategorias = response.data.length;
          }
          resolve();
        },
        error: () => resolve()
      });
    });
  }

  private cargarComisionGlobal(): Promise<void> {
    return new Promise((resolve) => {
      this.comisionService.obtenerComisionGlobal().subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.estadisticas.comisionGlobal = response.data.porcentaje;
          }
          resolve();
        },
        error: () => resolve()
      });
    });
  }

  private cargarEmpresasConComision(): Promise<void> {
    return new Promise((resolve) => {
      this.comisionService.obtenerComisionesEmpresas().subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.estadisticas.empresasConComision = response.data.length;
          }
          resolve();
        },
        error: () => resolve()
      });
    });
  }

  cerrarSesion() {
        this.authService.logout();
    }

  navegar(ruta: string): void {
    this.router.navigate([ruta]);
  }
}
