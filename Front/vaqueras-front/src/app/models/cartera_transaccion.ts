export interface Cartera {
    idUsuario: number;
    saldo: number;
}

export interface Recarga {
    idUsuario: number;
    monto: number;
    fecha: string; // YYYY-MM-DD
}
export interface Transaccion {
    idTransaccion: number;
    idUsuario: number;
    monto: number;
    tipo: 'RECARGA' | 'COMPRA';
    fecha: string; // YYYY-MM-DD
    comisionAplicada?: number;
    gananciaEmpresa?: number;
    gananciaPlataforma?: number;
}

export interface FiltroTransacciones {
    tipo?: 'RECARGA' | 'COMPRA';
    fechaInicio?: string;
    fechaFin?: string;
}

export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data?: T;
}