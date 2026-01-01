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
                path: 'jugador/tienda',
                loadComponent: () => import('./components/jugador/tienda/tienda')
                    .then(m => m.Tienda)
            },
            {
                path: 'dashboard',
                loadComponent: () => import('./components/administrador/dashboard/dashboard')
                    .then(m => m.DashboardAdmin)
            },
            {
                path: 'categorias',
                loadComponent: () => import('./components/administrador/categorias/categorias')
                    .then(m => m.Categorias)
            },
            {
                path: 'comprar/:id',
                loadComponent: ()=> import ('./components/jugador/comprar/comprar')
                .then(m => m.Comprar),
                canActivate: [authGuard]
            }

    /*
    {
        path: 'admin',
        canActivate: [authGuard, roleGuard],
        data: { roles: [Rol.ADMINISTRADOR] },
        children: [
            {
                path: 'dashboard',
                loadComponent: () => import('./components/admin/dashboard/dashboard.component')
                    .then(m => m.AdminDashboardComponent)
            },
            {
                path: 'jugadores',
                loadComponent: () => import('./components/admin/jugadores/jugadores.component')
                    .then(m => m.JugadoresComponent)
            },
            {
                path: 'empresas',
                loadComponent: () => import('./components/admin/empresas/empresas.component')
                    .then(m => m.EmpresasComponent)
            },
            {
                path: 'categorias',
                loadComponent: () => import('./components/admin/categorias/categorias.component')
                    .then(m => m.CategoriasComponent)
            },
            {
                path: 'banners',
                loadComponent: () => import('./components/admin/banners/banners.component')
                    .then(m => m.BannersComponent)
            },
            {
                path: 'reportes',
                loadComponent: () => import('./components/admin/reportes/reportes.component')
                    .then(m => m.ReportesAdminComponent)
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
                loadComponent: () => import('./components/empresa/dashboard/dashboard.component')
                    .then(m => m.EmpresaDashboardComponent)
            },
            {
                path: 'juegos',
                loadComponent: () => import('./components/empresa/juegos/juegos.component')
                    .then(m => m.EmpresaJuegosComponent)
            },
            {
                path: 'juegos/crear',
                loadComponent: () => import('./components/empresa/juegos/crear-juego/crear-juego.component')
                    .then(m => m.CrearJuegoComponent)
            },
            {
                path: 'juegos/:id',
                loadComponent: () => import('./components/empresa/juegos/detalle-juego/detalle-juego.component')
                    .then(m => m.DetalleJuegoEmpresaComponent)
            },
            {
                path: 'usuarios',
                loadComponent: () => import('./components/empresa/usuarios/usuarios.component')
                    .then(m => m.UsuariosEmpresaComponent)
            },
            {
                path: 'reportes',
                loadComponent: () => import('./components/empresa/reportes/reportes.component')
                    .then(m => m.ReportesEmpresaComponent)
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
                loadComponent: () => import('./components/jugador/dashboard/dashboard.component')
                    .then(m => m.JugadorDashboardComponent)
            },
            {
                path: 'tienda',
                loadComponent: () => import('./components/jugador/tienda/tienda.component')
                    .then(m => m.TiendaComponent)
            },
            {
                path: 'juego/:id',
                loadComponent: () => import('./components/jugador/detalle-juego/detalle-juego.component')
                    .then(m => m.DetalleJuegoComponent)
            },
            {
                path: 'biblioteca',
                loadComponent: () => import('./components/jugador/biblioteca/biblioteca.component')
                    .then(m => m.BibliotecaComponent)
            },
            {
                path: 'cartera',
                loadComponent: () => import('./components/jugador/cartera/cartera.component')
                    .then(m => m.CarteraComponent)
            },
            {
                path: 'grupos',
                loadComponent: () => import('./components/jugador/grupos/grupos.component')
                    .then(m => m.GruposComponent)
            },
            {
                path: 'perfil',
                loadComponent: () => import('./components/jugador/perfil/perfil.component')
                    .then(m => m.PerfilComponent)
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
        loadComponent: () => import('./components/shared/not-found/not-found.component')
            .then(m => m.NotFoundComponent)
    }
            */
];

