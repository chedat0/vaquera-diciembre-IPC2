import { EstadoInstalacion } from "./enums";

export interface InstalacionJuego {
    idUsuario: number;
    idJuego: number;
    esPrestado: boolean;
    estado: EstadoInstalacion;
    fechaEstado: string;
    tituloJuego?: string;
    idPropietario?: number;
    nombrePropietario?: string;
}