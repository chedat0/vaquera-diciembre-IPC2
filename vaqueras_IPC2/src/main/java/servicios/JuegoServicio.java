/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicios;

import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import daos.JuegoDAO;
import dtos.JuegoDTO;
import com.mycompany.vaqueras_ipc2.modelo.Juego;
import java.sql.*;
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
    
    public List<JuegoDTO> listarActivos() {
        List<JuegoDTO> juegosActivos = new ArrayList<>();
        String sql = "SELECT j.*, e.nombre AS nombre_empresa "
                + "FROM juegos j "
                + "LEFT JOIN empresas e ON j.id_empresa = e.id_empresa "
                + "WHERE j.venta_activa = TRUE "
                + // ← SOLO ACTIVOS
                "ORDER BY j.fecha_publicacion DESC";
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        try (Connection conn = connMySQL.conectar(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                JuegoDTO juego = new JuegoDTO();
                juego.setIdJuego(rs.getInt("id_juego"));
                juego.setTitulo(rs.getString("titulo"));
                juego.setDescripcion(rs.getString("descripcion"));
                juego.setPrecio(rs.getDouble("precio"));
                juego.setFechaLanzamiento(rs.getDate("fecha_publicacion").toLocalDate());                
                juego.setIdEmpresa(rs.getInt("id_empresa"));
                juego.setNombreEmpresa(rs.getString("nombre_empresa"));
                juego.setClasificacionPorEdad(rs.getString("clasificacion_por_edad"));
                juegosActivos.add(juego);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }

        return juegosActivos;
    }
}
