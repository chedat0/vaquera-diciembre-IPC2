export interface Empresa {
    idEmpresa: number;
    nombre: string;
    direccion?: string;
    telefono?: string;
    correo?: string;
    porcentajeComision?: number;
}

export interface UsuarioEmpresa {
    idUsuario: number;
    correo: string;
    nombre: string;
    fechaNacimiento: string;
    idEmpresa: number;
    nombreEmpresa?: string;
}

