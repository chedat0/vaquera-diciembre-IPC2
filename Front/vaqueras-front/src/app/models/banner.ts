export interface Banner {
    idBanner?: number;
    idJuego: number;
    ordenPrioridad: number;
    activo?: boolean;
    fechaCreacion?: Date;
    tituloJuego?: string;
    imagenUrl?: string;
}

export interface JuegoBanner {
    idJuego: number;
    titulo: string;
    precio: number;
    ventas: number;
    calificacionPromedio: number;
    totalComentarios: number;
    scoreBalance: number; 
}