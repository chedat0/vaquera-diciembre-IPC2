import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})

export class Login {
  correo: string = '';
  password: string = '';
  error: string = '';
  cargando: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (!this.correo || !this.password) {
      this.error = 'Por favor completa todos los campos';
      return;
    }

    this.cargando = true;
    this.error = '';

    this.authService.login(this.correo, this.password).subscribe({
      next: (response) => {
        if (response.success) {
          console.log('Login exitoso:', response.data);
          // Redirigir según el rol
          this.redirigirSegunRol(response.data?.idRol);
        } else {
          this.error = response.message;
        }
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error de login:', err);
        this.error = err.error?.message || 'Error al iniciar sesión';
        this.cargando = false;
      }
    });
  }

  private redirigirSegunRol(idRol?: number): void {
    switch (idRol) {
      case 1: // Administrador
        this.router.navigate(['/admin/dashboard']);
        break;
      case 2: // Empresa
        this.router.navigate(['/empresa/dashboard']);
        break;
      case 3: // Gamer
        this.router.navigate(['/tienda']);
        break;
      default:
        this.router.navigate(['/']);
    }
  }
}
