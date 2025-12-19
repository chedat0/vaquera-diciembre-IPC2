/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicios;

import daos.JuegoDAO;
import dtos.JuegoDTO;
import com.mycompany.vaqueras_ipc2.modelo.Juego;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeffm
 */

public class JuegoServicio {
    private JuegoDAO juegoDAO;
    
    public JuegoServicio() {
        this.juegoDAO = new JuegoDAO();
    }
    
    public List<JuegoDTO> listarTodos() {
        List<Juego> juegos = juegoDAO.listarTodos();
        return convertirADTOs(juegos);
    }
    
    public JuegoDTO buscarPorId(Integer idJuego) {
        Juego juego = juegoDAO.buscarPorId(idJuego);
        return juego != null ? convertirADTO(juego) : null;
    }
    
    public List<JuegoDTO> listarPorEmpresa(Integer idEmpresa) {
        List<Juego> juegos = juegoDAO.listarPorEmpresa(idEmpresa);
        return convertirADTOs(juegos);
    }
    
    public boolean crear(Juego juego) {
        return juegoDAO.crear(juego);
    }
    
    public boolean actualizar(Juego juego) {
        return juegoDAO.actualizar(juego);
    }
    
    public boolean desactivarVenta(Integer idJuego) {
        return juegoDAO.desactivarVenta(idJuego);
    }
    
    public List<JuegoDTO> buscarPorTitulo(String titulo) {
        List<Juego> juegos = juegoDAO.buscarPorTitulo(titulo);
        return convertirADTOs(juegos);
    }
    
    private JuegoDTO convertirADTO(Juego juego) {
        return new JuegoDTO(
            juego.getIdJuego(),
            juego.getIdEmpresa(),
            juego.getNombreEmpresa(),
            juego.getTitulo(),
            juego.getDescripcion(),
            juego.getRequisitosMinimos(),
            juego.getPrecio(),
            juego.getClasificacionPorEdad(),
            juego.getFechaLanzamiento(),
            juego.getVentaActiva()
        );
    }
    
    private List<JuegoDTO> convertirADTOs(List<Juego> juegos) {
        List<JuegoDTO> dtos = new ArrayList<>();
        for (Juego juego : juegos) {
            dtos.add(convertirADTO(juego));
        }
        return dtos;
    }
}
