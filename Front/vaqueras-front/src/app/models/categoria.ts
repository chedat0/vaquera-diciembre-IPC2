export interface Categoria {
    idCategoria?: number;
    nombre: string;
    activado?: boolean;
}

export interface CategoriaResponse {
    success: boolean;
    message: string;
    data: Categoria | Categoria[] | null;
}
