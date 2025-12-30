export interface Comentario {
    idComentario: number;
    idUsuario: number;
    idJuego: number;
    contenido: string;
    fechaComentario: string;
    nicknameUsuario?: string;
    tituloJuego?: string;
    respuestas?: Respuesta[];
}

export interface CreateComentarioRequest {
    idUsuario: number;
    idJuego: number;
    contenido: string;
}

export interface Respuesta {
    idRespuesta: number;
    idComentario: number;
    idUsuario: number;
    contenido: string;
    fechaRespuesta: string;
    nicknameUsuario?: string;
}

export interface CreateRespuestaRequest {
    idComentario: number;
    idUsuario: number;
    contenido: string;
}