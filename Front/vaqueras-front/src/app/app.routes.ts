import { Routes } from '@angular/router';
import { Login } from './components/login/login';
import { Registro } from './components/registro/registro';
import { authGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';
import { Rol } from './models/enums';


export const routes: Routes = [
    { path: '', redirectTo: '/login', pathMatch: 'full' },
    { path: 'login', component: Login },
    { path: 'registro', component: Registro },

    {
        path: 'admin',
        canActivate: [authGuard, roleGuard],
        data: { roles: [Rol.ADMINISTRADOR] },
        children: [
            {
                path: 'dashboard',
                loadComponent: () => import('./components/administrador/dashboard/dashboard')
                    .then(m => m.DashboardAdmin)
            },
            {
                path: 'jugadores',
                loadComponent: () => import('./components/administrador/banear/banear')
                    .then(m => m.BanearJugadores)
            },
            {
                path: 'empresas',
                loadComponent: () => import('./components/administrador/empresas/empresas')
                    .then(m => m.Empresas)
            },
            {
                path: 'comisiones',
                loadComponent: () => import ('./components/administrador/comisiones/comisiones')
                .then(m => m.GestionComisionesComponent)
            },
            {
                path: 'categorias',
                loadComponent: () => import('./components/administrador/categorias/categorias')
                    .then(m => m.Categorias)
            },
            {
                path: 'banners',
                loadComponent: () => import('./components/administrador/banner/banner')
                    .then(m => m.GestionBanners)
            },
            {
                path: 'reportes',
                loadComponent: () => import('./components/administrador/reporte-admin/reportes-admin')
                    .then(m => m.ReportesAdmin)
            },
            {
                path: 'usuarios-admin',
                loadComponent: () => import ('./components/administrador/usuarios-admin/usuarios-admin')
                .then(m => m.GestionAdministradores)
            },
            {
                path: '',
                redirectTo: 'dashboard',
                pathMatch: 'full'
            }
        ]
    },

    // Rutas de Dueño de Empresa (Rol = 2)
    {
        path: 'empresa',
        canActivate: [authGuard, roleGuard],
        data: { roles: [Rol.DUENO_EMPRESA] },
        children: [
            {
                path: 'dashboard',
                loadComponent: () => import('./components/empresa/dashboard/dashboard')
                    .then(m => m.DashboardEmpresa)
            },
            {
                path: 'juegos',
                loadComponent: () => import('./components/empresa/juegos/juegos')
                    .then(m => m.MisJuegosEmpresa)
            },
            {
                path: 'crear-juego',
                loadComponent: () => import('./components/empresa/crear-juego/crear')
                    .then(m => m.CrearJuego)
            },
            {
                path: 'usuarios-empresa',
                loadComponent: () => import('./components/empresa/usuarios-empresa/usuarios-empresa')
                    .then(m => m.UsuariosEmpresa)
            },
            {
                path: 'reportes',
                loadComponent: () => import('./components/empresa/reportes-empresa/reportes-empresa')
                    .then(m => m.ReportesEmpresa)
            },            
            {
                path: '',
                redirectTo: 'dashboard',
                pathMatch: 'full'
            }
        ]
    },

    // Rutas de Jugador (Rol = 3)
    {
        path: 'jugador',
        canActivate: [authGuard, roleGuard],
        data: { roles: [Rol.JUGADOR] },
        children: [
            {
                path: 'dashboard',
                loadComponent: () => import('./components/jugador/dashboard/dashboard')
                    .then(m => m.DashboardJugador)
            },
            {
                path: 'tienda',
                loadComponent: () => import('./components/jugador/tienda/tienda')
                    .then(m => m.Tienda)
            },
            {
                path: 'comprar',
                loadComponent: () => import('./components/jugador/comprar/comprar')
                    .then(m => m.Comprar)
            },
            {
                path: 'comentarios',
                loadComponent: () => import('./components/jugador/comentarios/comentario')
                    .then(m => m.Comentarios)
            },
            {
                path: 'biblioteca',
                loadComponent: () => import('./components/jugador/biblioteca/biblioteca')
                    .then(m => m.BibliotecaComponent)
            },
            {
                path: 'cartera',
                loadComponent: () => import('./components/jugador/cartera/cartera')
                    .then(m => m.CarteraComponent)
            },
            {
                path: 'grupos',
                loadComponent: () => import('./components/jugador/grupos/grupos')
                    .then(m => m.GruposComponent)
            },
            {
                path: 'perfil',
                loadComponent: () => import('./components/jugador/perfil/perfil')
                    .then(m => m.PerfilJugadores)
            },
            {
                path: 'reportes',
                loadComponent: () => import('./components/jugador/reportes-jugador/reportes-jugador')
                    .then(m => m.ReportesJugadorComponent)
            },
            {
                path: '',
                redirectTo: 'dashboard',
                pathMatch: 'full'
            }
        ]
    },

    // Ruta 404
    {
        path: '**',
        redirectTo: '/login'
    }

];

