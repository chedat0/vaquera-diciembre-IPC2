export interface Cartera {
    idUsuario: number;
    saldo: number;
}

export interface Transaccion {
    idTransaccion?: number;
    idUsuario: number;
    tipo: 'COMPRA' | 'RECARGA';
    monto: number;
    fecha: string;
    descripcion?: string;
}

export interface RecargaRequest {
    idUsuario: number;
    monto: number;
}