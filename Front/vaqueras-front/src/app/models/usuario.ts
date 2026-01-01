import {Rol} from '../models/enums';

export interface Usuario {
    idUsuario: number;
    nickname: string;
    correo: string;
    pais?: string;
    idRol: Rol;
    idEmpresa?: number;  // Se carga después para usuarios empresa
    nombreEmpresa?: string;
    fechaNacimiento?: string;
    telefono?: string;
    bibliotecaPublica?: boolean;
}

export interface LoginRequest {
    correo: string;
    password: string;
}

export interface RegistroRequest {
    nickname: string;
    correo: string;
    password: string;
    fechaNacimiento: string;
    telefono?: string;
    pais: string;
    idRol: Rol;
}