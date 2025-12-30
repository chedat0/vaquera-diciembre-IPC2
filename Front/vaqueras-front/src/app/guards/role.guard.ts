import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth';

export const roleGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    const currentUser = authService.getCurrentUser();

    if (!currentUser) {
        router.navigate(['/login']);
        return false;
    }

    // Obtener los roles permitidos desde la configuración de la ruta
    const rolesPermitidos = route.data['roles'] as number[];

    if (rolesPermitidos && rolesPermitidos.includes(currentUser.idRol)) {
        return true;
    }

    // Redirigir al dashboard correspondiente si no tiene permiso
    router.navigate([getDashboardByRole(currentUser.idRol)]);
    return false;
};

function getDashboardByRole(idRol: number): string {
    switch (idRol) {
        case 1:
            return '/admin/dashboard';
        case 2:
            return '/empresa/dashboard';
        case 3:
            return '/jugador/dashboard';
        default:
            return '/login';
    }
}