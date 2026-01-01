import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { JuegoService } from '../../../services/juego';
import { Juego } from '../../../models/juego';
import { EmpresaService } from '../../../services/empresa';
import { CategoriaService } from '../../../services/categoria';
import { BannerService } from '../../../services/banner';


@Component({
  selector: 'app-tienda',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tienda.html',
  styleUrl: './tienda.css',
})
export class Tienda implements OnInit {
  juegos: Juego[] = [];
  juegosFiltrados: Juego[] = [];
  juegosBanner: any[] = [];  // Ya vienen ordenados del backend
  categorias: any[] = [];
  empresas: any[] = [];

  // Filtros
  busqueda = '';
  categoriaSeleccionada = '';
  empresaSeleccionada = '';
  precioMin = 0;
  precioMax = 1000;
  clasificacionSeleccionada = '';

  // Ordenamiento
  ordenamiento = 'balance'; // balance, precio-asc, precio-desc, calificacion

  // Paginación
  paginaActual = 1;
  juegosPorPagina = 12;
  juegosPaginados: Juego[] = [];

  // Estados
  cargando = false;
  error = '';

  // Carrusel Banner
  indiceBannerActual = 0;

  constructor(
    private router: Router,
    private bannerService: BannerService,
    private juegoService: JuegoService,
    private categoriaService: CategoriaService,
    private empresaService: EmpresaService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
    this.iniciarCarrusel();
  }

  cargarDatos(): void {
    this.cargando = true;
        
    this.bannerService.obtenerMejorBalance(5).subscribe({
      next: (response) => {
        if (response.success) {
          this.juegosBanner = response.data || [];
        }
      },
      error: (err) => {
        console.error('Error al cargar banner:', err);
      }
    });
        
    this.juegoService.obtenerActivos().subscribe({
      next: (response) => {
        if (response.success) {
          this.juegos = response.data || [];
          this.aplicarFiltros();
        }
        this.cargando = false;
      },
      error: (err) => {
        this.error = 'Error al cargar juegos';
        this.cargando = false;
        console.error(err);
      }
    });
        
    this.categoriaService.obtenerTodas().subscribe({
      next: (response) => {
        if (response.success) {
          this.categorias = response.data || [];
        }
      }
    });
        
    this.empresaService.obtenerTodas().subscribe({
      next: (response) => {
        if (response.success) {
          this.empresas = response.data || [];
        }
      }
    });
  }

  aplicarFiltros(): void {
    let resultado = [...this.juegos];

    // Filtro por búsqueda
    if (this.busqueda.trim()) {
      const termino = this.busqueda.toLowerCase();
      resultado = resultado.filter(j => 
        j.titulo.toLowerCase().includes(termino) ||
        j.descripcion.toLowerCase().includes(termino)
      );
    }

    // Filtro por categoría
    if (this.categoriaSeleccionada) {
      resultado = resultado.filter(j =>
        j.categorias.some(cat => cat === this.categoriaSeleccionada)
      );
    }

    // Filtro por empresa
    if (this.empresaSeleccionada) {
      resultado = resultado.filter(j =>
        j.nombreEmpresa === this.empresaSeleccionada
      );
    }

    // Filtro por precio
    resultado = resultado.filter(j =>
      j.precio >= this.precioMin && j.precio <= this.precioMax
    );

    // Filtro por clasificación
    if (this.clasificacionSeleccionada) {
      resultado = resultado.filter(j =>
        j.clasificacionEdad === this.clasificacionSeleccionada
      );
    }

    // Aplicar ordenamiento
    this.ordenarJuegos(resultado);

    this.juegosFiltrados = resultado;
    this.paginaActual = 1;
    this.paginar();
  }

  ordenarJuegos(juegos: Juego[]): void {
    switch (this.ordenamiento) {
      case 'balance':       
        juegos.sort((a, b) => {
          if (a.scoreBalance !== undefined && b.scoreBalance !== undefined) {
            return b.scoreBalance - a.scoreBalance;
          }
          return (b.calificacionPromedio ?? 0) - (a.calificacionPromedio ?? 0);
        });
        break;
      case 'precio-asc':
        juegos.sort((a, b) => a.precio - b.precio);
        break;
      case 'precio-desc':
        juegos.sort((a, b) => b.precio - a.precio);
        break;
      case 'calificacion':
        juegos.sort((a, b) => 
          (b.calificacionPromedio ?? 0) - (a.calificacionPromedio ?? 0)
        );
        break;
    }
  }

  paginar(): void {
    const inicio = (this.paginaActual - 1) * this.juegosPorPagina;
    const fin = inicio + this.juegosPorPagina;
    this.juegosPaginados = this.juegosFiltrados.slice(inicio, fin);
  }

  cambiarPagina(pagina: number): void {
    this.paginaActual = pagina;
    this.paginar();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  get totalPaginas(): number {
    return Math.ceil(this.juegosFiltrados.length / this.juegosPorPagina);
  }

  get paginasArray(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, i) => i + 1);
  }

  verDetalle(juego: Juego): void {
    this.router.navigate(['/jugador/juego', juego.idJuego]);
  }

  // Carrusel del Banner
  iniciarCarrusel(): void {
    setInterval(() => {
      this.siguienteBanner();
    }, 5000);
  }

  siguienteBanner(): void {
    if (this.juegosBanner.length > 0) {
      this.indiceBannerActual = (this.indiceBannerActual + 1) % this.juegosBanner.length;
    }
  }

  anteriorBanner(): void {
    if (this.juegosBanner.length > 0) {
      this.indiceBannerActual = this.indiceBannerActual === 0
        ? this.juegosBanner.length - 1
        : this.indiceBannerActual - 1;
    }
  }

  irABanner(indice: number): void {
    this.indiceBannerActual = indice;
  }

  irAComprar(juego: Juego): void {
    this.router.navigate(['/comprar', juego.idJuego]);
  }


  limpiarFiltros(): void {
    this.busqueda = '';
    this.categoriaSeleccionada = '';
    this.empresaSeleccionada = '';
    this.precioMin = 0;
    this.precioMax = 1000;
    this.clasificacionSeleccionada = '';
    this.ordenamiento = 'balance';
    this.aplicarFiltros();
  }

  obtenerEstrellas(calificacion: number): string {
    const estrellas = '⭐'.repeat(Math.round(calificacion));
    const vacias = '☆'.repeat(5 - Math.round(calificacion));
    return estrellas + vacias;
  }

  obtenerClasificacionTexto(clasificacion: string): string {
    const textos: any = {
      'E': 'Para todos',
      'T': 'Adolescentes',
      'M': 'Jovenes',
      'AO': 'Adultos'
    };
    return textos[clasificacion] || clasificacion;
  }
}
