export interface Juego {
    idJuego?: number;
    idEmpresa: number;
    nombreEmpresa?: string;
    titulo: string;
    descripcion: string;
    requisitosMinimos: string;
    precio: number;
    clasificacionPorEdad: string;
    fechaLanzamiento: string;
    ventaActiva?: boolean;
    categorias?: string[];
    imagenPortada?: string;
}

export interface JuegoResponse {
    success: boolean;
    message: string;
    data: Juego | Juego[] | null;
}