import { Usuario } from "./usuario";

export interface Grupo {
    idGrupo: number;
    nombre: string;
    idCreador: number;
    fechaCreacion: string;
    miembros?: Usuario[];
    cantidadMiembros?: number;
}

export interface CreateGrupoRequest {
    nombre: string;
    idCreador: number;
}