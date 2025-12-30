import { Categoria } from "./categoria";

export interface Juego {
    idJuego: number;
    titulo: string;
    descripcion: string;
    precio: number;
    fechaPublicacion: string;
    imagenUrl?: string;
    idEmpresa: number;
    nombreEmpresa?: string;
    categorias?: Categoria[];
    promedioCalificacion?: number;
    totalVentas?: number;
}

export interface CreateJuegoRequest {
    titulo: string;
    descripcion: string;
    precio: number;
    fechaPublicacion: string;
    idEmpresa: number;
    categoriasIds?: number[];
}