/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import com.mycompany.vaqueras_ipc2.modelo.InstalacionJuego;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jeffm
 */
public class InstalacionJuegoDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    public InstalacionJuegoDAO() {
        conn = connMySQL.conectar();
    }
    
    //Registra o actualiza la instalacion de un juego
    public boolean registrarInstalacion(InstalacionJuego instalacion) {
        String sql = "INSERT INTO instalacion_juego (id_usuario, id_juego, es_prestado, estado, fecha_estado) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE estado = ?, fecha_estado = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instalacion.getIdUsuario());
            ps.setInt(2, instalacion.getIdJuego());
            ps.setBoolean(3, instalacion.isEsPrestado());
            ps.setString(4, instalacion.getEstado().name());
            ps.setDate(5, Date.valueOf(instalacion.getFechaEstado()));
            ps.setString(6, instalacion.getEstado().name());
            ps.setDate(7, Date.valueOf(instalacion.getFechaEstado()));
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Instalación registrada: Usuario " + instalacion.getIdUsuario() + 
                                 " | Juego " + instalacion.getIdJuego() + 
                                 " | Estado: " + instalacion.getEstado());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al registrar instalación: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    //obtiene todos los juegos que ha instalados en algun momento un usuario
    public List<InstalacionJuego> obtenerInstalacionesPorUsuario(int idUsuario) {
        List<InstalacionJuego> instalaciones = new ArrayList<>();
        
        String sql = "SELECT ij.id_usuario, ij.id_juego, ij.es_prestado, ij.estado, " +
                    "ij.fecha_estado, j.titulo " +
                    "FROM instalacion_juego ij " +
                    "INNER JOIN juego j ON ij.id_juego = j.id_juego " +
                    "WHERE ij.id_usuario = ? " +
                    "ORDER BY ij.estado DESC, j.titulo";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                InstalacionJuego inst = new InstalacionJuego();
                inst.setIdUsuario(rs.getInt("id_usuario"));
                inst.setIdJuego(rs.getInt("id_juego"));
                inst.setEsPrestado(rs.getBoolean("es_prestado"));
                inst.setEstado(InstalacionJuego.EstadoInstalacion.valueOf(rs.getString("estado")));
                inst.setFechaEstado(rs.getDate("fecha_estado").toLocalDate());
                inst.setTituloJuego(rs.getString("titulo"));
                
                instalaciones.add(inst);
            }
            
            System.out.println("Obtenidas " + instalaciones.size() + 
                             " instalaciones del usuario " + idUsuario);
            
        } catch (SQLException e) {
            System.err.println("Error al obtener instalaciones: " + e.getMessage());
        }
        
        return instalaciones;
    }
    
    //Obtiene unicamente los juegos instalados actualmente
     public List<InstalacionJuego> obtenerJuegosInstalados(int idUsuario) {
        List<InstalacionJuego> instalaciones = new ArrayList<>();
        
        String sql = "SELECT ij.id_usuario, ij.id_juego, ij.es_prestado, ij.estado, " +
                    "ij.fecha_estado, j.titulo " +
                    "FROM instalacion_juego ij " +
                    "INNER JOIN juego j ON ij.id_juego = j.id_juego " +
                    "WHERE ij.id_usuario = ? AND ij.estado = 'INSTALADO' " +
                    "ORDER BY ij.es_prestado, j.titulo";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                InstalacionJuego inst = new InstalacionJuego();
                inst.setIdUsuario(rs.getInt("id_usuario"));
                inst.setIdJuego(rs.getInt("id_juego"));
                inst.setEsPrestado(rs.getBoolean("es_prestado"));
                inst.setEstado(InstalacionJuego.EstadoInstalacion.INSTALADO);
                inst.setFechaEstado(rs.getDate("fecha_estado").toLocalDate());
                inst.setTituloJuego(rs.getString("titulo"));
                
                instalaciones.add(inst);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener juegos instalados: " + e.getMessage());
        }
        
        return instalaciones;
    }
     
    //cuenta los juegos prestados que tiene instalados un usuario 
    public int contarJuegosPrestadosInstalados(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM instalacion_juego " +
                    "WHERE id_usuario = ? AND es_prestado = true AND estado = 'INSTALADO'";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int cantidad = rs.getInt(1);
                System.out.println("Usuario " + idUsuario + " tiene " + cantidad + 
                                 " juegos prestados instalados");
                return cantidad;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar juegos prestados instalados: " + e.getMessage());
        }
        
        return 0;
    }
    
    //Actualiza el estado de una instalacion
    public boolean actualizarEstadoInstalacion(int idUsuario, int idJuego, 
                                              InstalacionJuego.EstadoInstalacion nuevoEstado,
                                              LocalDate fecha) {
        String sql = "UPDATE instalacion_juego SET estado = ?, fecha_estado = ? " +
                    "WHERE id_usuario = ? AND id_juego = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.name());
            ps.setDate(2, Date.valueOf(fecha));
            ps.setInt(3, idUsuario);
            ps.setInt(4, idJuego);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Estado actualizado: " + nuevoEstado);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
        }
        
        return false;
    }
    
    //verifica si existe un registro de instalacion
    public boolean existeInstalacion(int idUsuario, int idJuego) {
        String sql = "SELECT COUNT(*) FROM instalacion_juego WHERE id_usuario = ? AND id_juego = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idJuego);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar instalación: " + e.getMessage());
        }
        
        return false;
    }
    
    //Obtiene una instalacion especifica
    public InstalacionJuego obtenerInstalacion(int idUsuario, int idJuego) {
        String sql = "SELECT ij.id_usuario, ij.id_juego, ij.es_prestado, ij.estado, " +
                    "ij.fecha_estado, j.titulo " +
                    "FROM instalacion_juego ij " +
                    "INNER JOIN juego j ON ij.id_juego = j.id_juego " +
                    "WHERE ij.id_usuario = ? AND ij.id_juego = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idJuego);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                InstalacionJuego inst = new InstalacionJuego();
                inst.setIdUsuario(rs.getInt("id_usuario"));
                inst.setIdJuego(rs.getInt("id_juego"));
                inst.setEsPrestado(rs.getBoolean("es_prestado"));
                inst.setEstado(InstalacionJuego.EstadoInstalacion.valueOf(rs.getString("estado")));
                inst.setFechaEstado(rs.getDate("fecha_estado").toLocalDate());
                inst.setTituloJuego(rs.getString("titulo"));
                
                return inst;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener instalación: " + e.getMessage());
        }
        
        return null;
    }
    
    //Elimina un registro de instalacion
    public boolean eliminarInstalacion(int idUsuario, int idJuego) {
        String sql = "DELETE FROM instalacion_juego WHERE id_usuario = ? AND id_juego = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idJuego);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Instalación eliminada");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar instalación: " + e.getMessage());
        }
        
        return false;
    }
    
    //Obtiene todos los juegos disponibles para un usuario en su grupo familiar 
    public List<InstalacionJuego> obtenerJuegosDisponiblesGrupo(int idUsuario) {
        List<InstalacionJuego> juegos = new ArrayList<>();
        
        String sql = "SELECT DISTINCT j.id_juego, j.titulo, " +
                    "CASE WHEN l.id_usuario = ? THEN false ELSE true END as es_prestado, " +
                    "l.id_usuario as id_propietario, u.nickname as nombre_propietario " +
                    "FROM licencia l " +
                    "INNER JOIN juego j ON l.id_juego = j.id_juego " +
                    "INNER JOIN usuario u ON l.id_usuario = u.id_usuario " +
                    "WHERE l.id_usuario IN ( " +
                    "  SELECT gm.id_usuario FROM grupo_miembro gm " +
                    "  WHERE gm.id_grupo IN ( " +
                    "    SELECT id_grupo FROM grupo_miembro WHERE id_usuario = ? " +
                    "  ) " +
                    ") " +
                    "ORDER BY es_prestado, j.titulo";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                InstalacionJuego inst = new InstalacionJuego();
                inst.setIdUsuario(idUsuario);
                inst.setIdJuego(rs.getInt("id_juego"));
                inst.setTituloJuego(rs.getString("titulo"));
                inst.setEsPrestado(rs.getBoolean("es_prestado"));
                inst.setIdPropietario(rs.getInt("id_propietario"));
                inst.setNombreUsuario(rs.getString("nombre_propietario"));
                
                juegos.add(inst);
            }
            
            System.out.println("Obtenidos " + juegos.size() + 
                             " juegos disponibles para usuario " + idUsuario);
            
        } catch (SQLException e) {
            System.err.println("Error al obtener juegos disponibles: " + e.getMessage());
        }
        
        return juegos;
    }
    
    
}
