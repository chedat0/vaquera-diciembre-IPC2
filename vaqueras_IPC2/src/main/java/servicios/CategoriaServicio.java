/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicios;
import daos.CategoriaDAO;
import dtos.CategoriaDTO;
import com.mycompany.vaqueras_ipc2.modelo.Categoria;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jeffm
 */
public class CategoriaServicio {
    private CategoriaDAO categoriaDAO;
    
    public CategoriaServicio() {
        this.categoriaDAO = new CategoriaDAO();
    }
    
    public List<CategoriaDTO> listarTodas() {
        List<Categoria> categorias = categoriaDAO.listarTodas();
        return convertirADTOs(categorias);
    }
    
    public List<CategoriaDTO> listarActivas() {
        List<Categoria> categorias = categoriaDAO.listarActivas();
        return convertirADTOs(categorias);
    }
    
    public CategoriaDTO buscarPorId(Integer idCategoria) {
        Categoria categoria = categoriaDAO.buscarPorId(idCategoria);
        return categoria != null ? convertirADTO(categoria) : null;
    }
    
    public boolean crear(Categoria categoria) {        
        if (categoriaDAO.existeNombre(categoria.getNombre())) {
            return false;
        }
        return categoriaDAO.crear(categoria);
    }
    
    public boolean actualizar(Categoria categoria) {
        return categoriaDAO.actualizar(categoria);
    }
    
    public boolean eliminar(Integer idCategoria) {
        return categoriaDAO.eliminar(idCategoria);
    }
    
    private CategoriaDTO convertirADTO(Categoria categoria) {
        return new CategoriaDTO(
            categoria.getIdCategoria(),
            categoria.getNombre(),
            categoria.getActivado()
        );
    }
    
    private List<CategoriaDTO> convertirADTOs(List<Categoria> categorias) {
        List<CategoriaDTO> dtos = new ArrayList<>();
        for (Categoria categoria : categorias) {
            dtos.add(convertirADTO(categoria));
        }
        return dtos;
    }
}
