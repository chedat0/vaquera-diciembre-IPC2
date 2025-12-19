import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CategoriaService } from '../../../services/categoria';
import { Categoria } from '../../../models/categoria';

@Component({
  selector: 'app-categorias',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './categorias.html',
  styleUrl: './categorias.css',
})
export class Categorias implements OnInit{
  categorias: Categoria[] = [];
  categoriaEditando: Categoria | null = null;
  nuevaCategoria: Categoria = { nombre: '', activado: true };
  mostrarFormulario: boolean = false;

  constructor(private categoriaService: CategoriaService) {}

  ngOnInit(): void {
    this.cargarCategorias();
  }

  cargarCategorias(): void {
    this.categoriaService.listarTodas().subscribe({
      next: (response) => {
        if (response.success && Array.isArray(response.data)) {
          this.categorias = response.data;
        }
      },
      error: (err) => console.error('Error:', err)
    });
  }

  crearCategoria(): void {
    this.categoriaService.crear(this.nuevaCategoria).subscribe({
      next: (response) => {
        if (response.success) {
          alert('Categoría creada exitosamente');
          this.cargarCategorias();
          this.nuevaCategoria = { nombre: '', activado: true };
          this.mostrarFormulario = false;
        } else {
          alert(response.message);
        }
      },
      error: (err) => alert('Error al crear categoría')
    });
  }

  editarCategoria(categoria: Categoria): void {
    this.categoriaEditando = { ...categoria };
  }

  guardarEdicion(): void {
    if (this.categoriaEditando) {
      this.categoriaService.actualizar(this.categoriaEditando).subscribe({
        next: (response) => {
          if (response.success) {
            alert('Categoría actualizada');
            this.cargarCategorias();
            this.categoriaEditando = null;
          }
        },
        error: (err) => alert('Error al actualizar')
      });
    }
  }

  eliminarCategoria(id: number): void {
    if (confirm('¿Estás seguro de eliminar esta categoría?')) {
      this.categoriaService.eliminar(id).subscribe({
        next: (response) => {
          if (response.success) {
            alert('Categoría eliminada');
            this.cargarCategorias();
          }
        },
        error: (err) => alert('Error al eliminar')
      });
    }
  }

  cancelarEdicion(): void {
    this.categoriaEditando = null;
  }
}
