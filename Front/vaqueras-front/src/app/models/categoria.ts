export interface Categoria {
    idCategoria?: number;
    nombre: string;
    descripcion?: string;
    activado?: boolean;

}

export interface CategoriaResponse {
    success: boolean;
    message: string;
    data: Categoria | Categoria[] | null;
}
