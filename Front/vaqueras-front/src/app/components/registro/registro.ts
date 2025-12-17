import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './registro.html',
  styleUrl: './registro.css',
})
export class Registro {
  // Datos del formulario
  nickname: string = '';
  correo: string = '';
  password: string = '';
  confirmarPassword: string = '';
  fechaNacimiento: string = '';
  telefono: string = '';
  pais: string = '';

  // Control de Interfaz
  error: string = '';
  cargando: boolean = false;
  mostrarPassword: boolean = false;

  // Lista de países
  paises: string[] = [
    'Guatemala',
    'México',
    'El Salvador',
    'Honduras',
    'Costa Rica',
    'Nicaragua',
    'Panamá',
    'Estados Unidos',
    'España',
    'Argentina',
    'Colombia',
    'Chile',
    'Perú'
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  onSubmit(): void {
    // Limpiar error anterior
    this.error = '';

    // Validaciones
    if (!this.validarFormulario()) {
      return;
    }

    this.cargando = true;

    // Preparar datos para enviar
    const datosRegistro = {
      nickname: this.nickname,
      correo: this.correo,
      password: this.password,
      fechaNacimiento: this.fechaNacimiento,
      telefono: this.telefono,
      pais: this.pais,
      idRol: 3
    };

    // Llamar al servicio
    this.authService.registro(datosRegistro).subscribe({
      next: (response) => {
        if (response.success) {
          alert('¡Registro exitoso! Bienvenido al sistema');
          this.router.navigate(['/login']);
        } else {
          this.error = response.message;
        }
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error en registro:', err);
        this.error = err.error?.message || 'Error al registrar usuario';
        this.cargando = false;
      }
    });
  }

  validarFormulario(): boolean {
    // Validar campos vacíos
    if (!this.nickname || !this.correo || !this.password ||
      !this.confirmarPassword || !this.fechaNacimiento || !this.pais) {
      this.error = 'Todos los campos marcados con * son obligatorios';
      return false;
    }

    // Validar nickname
    if (this.nickname.length < 3) {
      this.error = 'El nickname debe tener al menos 3 caracteres';
      return false;
    }

    // Validar email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.correo)) {
      this.error = 'El correo electrónico no es válido';
      return false;
    }

    // Validar contraseña
    if (this.password.length < 4) {
      this.error = 'La contraseña debe tener al menos 4 caracteres';
      return false;
    }

    // Validar coincidencia de contraseñas
    if (this.password !== this.confirmarPassword) {
      this.error = 'Las contraseñas no coinciden';
      return false;
    }

    // Validar edad (debe ser mayor de 12 años)
    const fechaNac = new Date(this.fechaNacimiento);
    const hoy = new Date();
    let edad = hoy.getFullYear() - fechaNac.getFullYear();
    const mes = hoy.getMonth() - fechaNac.getMonth();

    if (mes < 0 || (mes === 0 && hoy.getDate() < fechaNac.getDate())) {
      edad--;
    }

    if (edad < 12) {
      this.error = 'Debes tener al menos 12 años para registrarte';
      return false;
    }

    return true;
  }
  
  getCurrentDate(): string {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  toggleMostrarPassword(): void {
    this.mostrarPassword = !this.mostrarPassword;
  }
}
