export interface Compra {
    idUsuario: number;
    idJuego: number;
    fechaCompra: string; // YYYY-MM-DD (manual para simular fechas pasadas)
}

export interface ResultadoCompra {
    juego: string;
    precio: number;
    comisionAplicada: number;
    nuevoSaldo: number;
    fechaCompra: string;
}

export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data?: T;
}