import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth';
import { Rol } from '../../../models/enums';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  usuario: any = null;
  menuAbierto = false;
  Rol = Rol; // Exponer enum al template

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.usuario = this.authService.getCurrentUser();
  }

  toggleMenu(): void {
    this.menuAbierto = !this.menuAbierto;
  }

  cerrarSesion(): void {
    try {      
      this.authService.logout();
      
      this.router.navigate(['/login']);
    } catch (err) {
      console.error('Error al cerrar sesión:', err);
      this.router.navigate(['/login']);
    }
  }

  esAdministrador(): boolean {
    return this.usuario?.idRol === Rol.ADMINISTRADOR;
  }

  esEmpresa(): boolean {
    return this.usuario?.idRol === Rol.DUENO_EMPRESA;
  }

  esJugador(): boolean {
    return this.usuario?.idRol === Rol.JUGADOR;
  }
}
