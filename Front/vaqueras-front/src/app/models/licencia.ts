export interface Licencia {
    idLicencia: number;
    idUsuario: number;
    idJuego: number;
    fechaCompra: string;
    tituloJuego?: string;
    nombreUsuario?: string;
}

export interface CompraJuegoRequest {
    idUsuario: number;
    idJuego: number;
}