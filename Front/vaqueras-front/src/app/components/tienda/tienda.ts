import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { JuegoService } from '../../services/juego';
import { Juego } from '../../models/juego';
import { LicenciaService } from '../../services/licencia';
import { EdadInfoService } from '../../services/edad_info';
import { AuthService } from '../../services/auth';
import { Usuario } from '../../models/usuario';
import { CompraJuegoRequest } from '../../models/licencia';
import { Categoria } from '../../models/categoria';


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
  categorias: Categoria[] = [];
  usuario: Usuario | null = null;

  // Búsqueda y filtros
  busqueda = '';
  categoriaSeleccionada: number | null = null;
  clasificacionSeleccionada = '';
  precioMin: number | null = null;
  precioMax: number | null = null;
  ordenamiento = 'recientes'; // 
  
  // Estado de compra
  loading = false;
  comprando = false;
  errorMessage = '';
  successMessage = '';  

  constructor(    
    private juegoService: JuegoService,
    private licenciaService: LicenciaService,
    private edadInfoService: EdadInfoService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.usuario = this.authService.getCurrentUser();    
    this.cargarJuegos();
  }
  
  cargarJuegos(): void {   
    this.loading = true;
    this.juegoService.getJuegosActivos().subscribe({
      next: (juegos) => {   
        this.juegos = juegos;
        this.aplicarFiltros();
        this.loading = false;
      },  
      error: (error) =>{
      console.error('Error al cargar juegos:', error);
      this.errorMessage = 'Error al cargar juegos';
      this.loading = false;
      }    
    });
    
  }

  aplicarFiltros(): void {
    let resultado = [...this.juegos];

    // Filtro de búsqueda por nombre
    if (this.busqueda.trim()) {
      const busquedaLower = this.busqueda.toLowerCase().trim();
      resultado = resultado.filter(juego => 
        juego.titulo.toLowerCase().includes(busquedaLower) ||
        juego.descripcion?.toLowerCase().includes(busquedaLower)
      );
    }

    // Filtro por categoría
    if (this.categoriaSeleccionada) {
      resultado = resultado.filter(juego => 
        juego.categorias?.some(cat => cat.idCategoria === this.categoriaSeleccionada)
      );
    }

    // Filtro por clasificación de edad
    if (this.clasificacionSeleccionada) {
      resultado = resultado.filter(juego => 
        juego.clasifiacionPorEdad?.toUpperCase() === this.clasificacionSeleccionada.toUpperCase()
      );
    }

    // Filtro por precio mínimo
    if (this.precioMin !== null && this.precioMin > 0) {
      resultado = resultado.filter(juego => juego.precio >= this.precioMin!);
    }

    // Filtro por precio máximo
    if (this.precioMax !== null && this.precioMax > 0) {
      resultado = resultado.filter(juego => juego.precio <= this.precioMax!);
    }

    // Ordenamiento
    this.ordenarJuegos(resultado);

    this.juegosFiltrados = resultado;
  }

  //ordena los juegos
  ordenarJuegos(juegos: Juego[]): void {
    switch (this.ordenamiento) {
      case 'precio-asc':
        juegos.sort((a, b) => a.precio - b.precio);
        break;
      case 'precio-desc':
        juegos.sort((a, b) => b.precio - a.precio);
        break;
      case 'nombre':
        juegos.sort((a, b) => a.titulo.localeCompare(b.titulo));
        break;
      case 'recientes':
        juegos.sort((a, b) => 
          new Date(b.fechaPublicacion).getTime() - new Date(a.fechaPublicacion).getTime()
        );
        break;
    }
  }

  //limpia filtros
  limpiarFiltros(): void {
    this.busqueda = '';
    this.categoriaSeleccionada = null;
    this.clasificacionSeleccionada = '';
    this.precioMin = null;
    this.precioMax = null;
    this.ordenamiento = 'recientes';
    this.aplicarFiltros();
  }

  comprarJuego(juego: Juego): void {
    if (!this.usuario) {
      this.errorMessage = 'Debes iniciar sesión para comprar';
      return;
    }

    this.comprando = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request: CompraJuegoRequest = {
      idUsuario: this.usuario.idUsuario,
      idJuego: juego.idJuego
    };
    
    this.licenciaService.comprarJuego(request).subscribe({
      next: (response) => {
        this.comprando = false;
        if (response.success) {
          this.successMessage = '¡Compra exitosa!';
        } else {
          // Mostrar error del backend (puede ser edad, saldo, etc)
          this.errorMessage = response.message || 'Error en la compra';
        }
      },
      error: (error) => {
        this.comprando = false;
        // Backend retornó error (403 si es problema de edad)
        this.errorMessage = error.error?.message || 
                          'No se pudo procesar la compra';
      }
    });
  }

  getColor(clasificacion: string): string {
    return this.edadInfoService.getColor(clasificacion);
  }

  getIcono(clasificacion: string): string {
    return this.edadInfoService.getIcono(clasificacion);
  }

  getTexto(clasificacion: string): string {
    return this.edadInfoService.getTexto(clasificacion);
  }

  getEdadMinima(clasificacion: string): number {
    return this.edadInfoService.getEdadMinima(clasificacion);
  }
}
