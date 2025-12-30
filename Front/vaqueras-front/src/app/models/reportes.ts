import { Juego } from "./juego";

export interface ReporteAdministrador {
    totalUsuarios: number;
    totalJuegos: number;
    totalEmpresas: number;
    ventasTotales: number;
    ingresosTotales: number;
    juegosMasVendidos: JuegoVentas[];
}

export interface ReporteEmpresa {
    idEmpresa: number;
    nombreEmpresa: string;
    totalJuegos: number;
    totalVentas: number;
    ingresosBrutos: number;
    comisionPlataforma: number;
    ingresosNetos: number;
    juegosMasVendidos: JuegoVentas[];
}

export interface ReporteUsuario {
    idUsuario: number;
    nickname: string;
    totalJuegos: number;
    gastoTotal: number;
    categoriasFavoritas: CategoriaEstadistica[];
    juegosRecientes: Juego[];
}

export interface JuegoVentas {
    idJuego: number;
    titulo: string;
    totalVentas: number;
    ingresos: number;
}

export interface CategoriaEstadistica {
    idCategoria: number;
    nombre: string;
    cantidad: number;
}