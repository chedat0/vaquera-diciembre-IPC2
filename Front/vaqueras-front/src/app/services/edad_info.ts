import { Injectable } from '@angular/core';


@Injectable({
    providedIn: 'root',
})
export class EdadInfoService {
    
    getColor(clasificacion: string): string {
        if (!clasificacion) return '#94a3b8';

        const cls = clasificacion.toUpperCase().trim();

        switch (cls) {
            case 'E':
                return '#4ade80';  // Verde - Para todos
            case 'T':
                return '#fbbf24';  // Amarillo - Adolescentes (12+)
            case 'M':
                return '#fb923c';  // Naranja - Jóvenes (16+)
            case 'AO':
                return '#ef4444';  // Rojo - Solo adultos (18+)
            default:
                return '#94a3b8';  // Gris - Desconocida
        }
    }

    /**
     * Obtiene el icono según clasificación ESRB
     */
    getIcono(clasificacion: string): string {
        if (!clasificacion) return '❓';

        const cls = clasificacion.toUpperCase().trim();

        switch (cls) {
            case 'E':
                return '👶';  // Para todos
            case 'T':
                return '👦';  // Adolescentes
            case 'M':
                return '👨';  // Jóvenes
            case 'AO':
                return '🔞';  // Solo adultos
            default:
                return '❓';
        }
    }

    //
    getTexto(clasificacion: string): string {
        if (!clasificacion) return 'Sin clasificación';

        const cls = clasificacion.toUpperCase().trim();

        switch (cls) {
            case 'E':
                return 'Para todos';
            case 'T':
                return 'Mayores de 12 años';
            case 'M':
                return 'Mayores de 16 años';
            case 'AO':
                return 'Solo adultos';
            default:
                return clasificacion;
        }
    }

    //obtiene edad minima del usuario
    getEdadMinima(clasificacion: string): number {
        if (!clasificacion) return 0;

        const cls = clasificacion.toUpperCase().trim();

        switch (cls) {
            case 'E':
                return 0;   // Sin restricción
            case 'T':
                return 12;  // Adolescentes
            case 'M':
                return 16;  // Jóvenes
            case 'AO':
                return 18;  // Solo adultos
            default:
                return 0;
        }
    }

    /**
     * Obtiene texto corto para el badge
     */
    getTextoBadge(clasificacion: string): string {
        if (!clasificacion) return '';

        const edad = this.getEdadMinima(clasificacion);

        if (edad === 0) return 'Todos';
        return `${edad}+`;
    }
}