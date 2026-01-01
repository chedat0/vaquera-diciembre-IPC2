export interface ComisionGlobal {
    porcentaje: number;
    fechaModificacion?: Date;
}

export interface ComisionEmpresa {
    idEmpresa: number;
    nombreEmpresa?: string;
    porcentajeEspecifico: number;
    fechaAsignacion?: Date;
}
export interface EmpresaConComision {
    idEmpresa: number;
    nombreEmpresa: string;
    comisionEspecifica: number;
}

export interface HistorialComision {
    fecha: string;
    porcentajeAnterior: number;
    porcentajeNuevo: number;
    usuarioModificador: string;
}