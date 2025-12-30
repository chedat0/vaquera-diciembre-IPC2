export interface Calificacion {
    idCalificacion: number;
    idJuego: number;
    idUsuario: number;
    puntuacion: number; // 1-5 estrellas
    fechaCalificacion: string;
}

export interface CreateCalificacionRequest {
    idJuego: number;
    idUsuario: number;
    puntuacion: number;
}