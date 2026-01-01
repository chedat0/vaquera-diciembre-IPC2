export interface Instalacion {
    idInstalacion?: number;
    idUsuario: number;
    idJuego: number;
    fechaEstado: string; // YYYY-MM-DD
    estado: 'INSTALADO' | 'NO_INSTALADO';
    esPrestado: boolean;
    tituloJuego?: string;
    nombrePropietario?: string;
}

