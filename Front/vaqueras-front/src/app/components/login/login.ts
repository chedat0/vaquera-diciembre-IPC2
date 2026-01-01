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
  ) { }

  onSubmit(): void {
    if (!this.correo || !this.password) {
      this.error = 'Por favor completa todos los campos';
      return;
    }

    this.cargando = true;
    this.error = '';

    this.authService.login( this.correo, this.password ).subscribe({
      next: (response) => {
        if (response.success) {
          this.cargando = false;
          console.log('Login exitoso:', response.data);
          // Redirigir según el rol
          if (this.authService.isAuthenticated()) {
            const currentUser = this.authService.getCurrentUser();
            if (currentUser) {
              this.authService.redirectToDashboard(currentUser.idRol);
            }
          }
        } else {
          this.error = response.message;
        }
        this.cargando = false;
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error de login:', err);
        this.error = err.error?.message || 'Error al iniciar sesión';
        this.cargando = false;
      }
    });
  }

}
