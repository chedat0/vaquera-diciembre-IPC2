export interface Comentario {
    idComentario?: number;
    idJuego: number;
    idUsuario: number;
    contenido: string;
    calificacion: number; 
    fecha: string; // YYYY-MM-DD
    visible?: boolean;
    nombreUsuario?: string;
    tituloJuego?: string;
    cantidadRespuestas?: number;
    respuestas?: Respuesta[];
}

export interface Respuesta {
    idRespuesta?: number;
    idComentario: number;
    idUsuario: number;
    contenido: string;
    fecha: string;
    nombreUsuario?: string;
}

export interface PromedioCalificacion {
    idJuego: number;
    promedioCalificacion: number;
    totalComentarios: number;
}

export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data?: T;
}