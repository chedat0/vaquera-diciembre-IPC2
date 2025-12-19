import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { JuegoService } from '../../services/juego';
import { Juego } from '../../models/juego';

@Component({
  selector: 'app-tienda',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './tienda.html',
  styleUrl: './tienda.css',
})
export class Tienda implements OnInit {
  juegos: Juego[] = [];
  juegosFiltrados: Juego[] = [];
  busqueda: string = '';
  cargando: boolean = false;

  constructor(
    private juegoService: JuegoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarJuegos();
  }

  cargarJuegos(): void {
    this.cargando = true;
    this.juegoService.listarTodos().subscribe({
      next: (response) => {
        if (response.success && Array.isArray(response.data)) {
          this.juegos = response.data;
          this.juegosFiltrados = response.data;
        }
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar juegos:', err);
        this.cargando = false;
      }
    });
  }

  buscarJuegos(): void {
    if (this.busqueda.trim() === '') {
      this.juegosFiltrados = this.juegos;
    } else {
      this.juegoService.buscarPorTitulo(this.busqueda).subscribe({
        next: (response) => {
          if (response.success && Array.isArray(response.data)) {
            this.juegosFiltrados = response.data;
          }
        },
        error: (err) => {
          console.error('Error en búsqueda:', err);
        }
      });
    }
  }

  verDetalle(idJuego: number): void {
    this.router.navigate(['/juego', idJuego]);
  }
}
