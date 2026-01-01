import { Categoria } from "./categoria";

export interface Juego {
    idJuego: number;
    titulo: string;
    descripcion: string;
    precio: number;
    clasificacionEdad: string;
    imagenPrincipal?: string;
    calificacionPromedio: number;
    nombreEmpresa: string;
    categorias: string[];
    idEmpresa:number;
    fechaLanzamiento: string;
    activo: boolean;
    
    // Datos adicionales del algoritmo (si vienen)
    ventas?: number;
    scoreBalance?: number;
}

export interface CreateJuegoRequest {
    titulo: string;
    descripcion: string;
    precio: number;
    clasificacion: string;
    categorias: string []; 
    idEmpresa: number;
    fechaCreacion: string;
    comentariosActivos: boolean;
}