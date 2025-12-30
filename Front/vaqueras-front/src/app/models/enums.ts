export enum Rol {
    ADMINISTRADOR = 1,
    DUENO_EMPRESA = 2,
    JUGADOR = 3
}

export enum EstadoInstalacion {
    INSTALADO = 'INSTALADO',
    DESCARGANDO = 'DESCARGANDO',
    PAUSADO = 'PAUSADO',
    DESINSTALADO = 'DESINSTALADO'
}

export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data: T | null;
}