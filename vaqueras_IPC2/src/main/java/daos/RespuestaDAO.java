/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import com.mycompany.vaqueras_ipc2.modelo.Respuesta;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jeffm
 */
public class RespuestaDAO {
    
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    
    public RespuestaDAO() {
        conn = connMySQL.conectar();
    }
    
    //Crea una respuesta a un comentario
    public boolean crearRespuesta(Respuesta respuesta) {
        String sql = "INSERT INTO respuesta (id_comentario, id_usuario, contenido, fecha) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, respuesta.getIdComentario());
            ps.setInt(2, respuesta.getIdUsuario());
            ps.setString(3, respuesta.getContenido());
            ps.setDate(4, Date.valueOf(respuesta.getFecha()));
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    respuesta.setIdRespuesta(rs.getInt(1));
                }
                
                System.out.println("Respuesta creada: ID " + respuesta.getIdRespuesta());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al crear respuesta: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    //Obtiene todas las respuestas de un comentario
    public List<Respuesta> obtenerRespuestasPorComentario(int idComentario) {
        List<Respuesta> respuestas = new ArrayList<>();
        
        String sql = "SELECT r.id_respuesta, r.id_comentario, r.id_usuario, r.contenido, " +
                    "r.fecha, u.nickname " +
                    "FROM respuesta r " +
                    "INNER JOIN usuario u ON r.id_usuario = u.id_usuario " +
                    "WHERE r.id_comentario = ? " +
                    "ORDER BY r.fecha ASC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idComentario);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Respuesta resp = new Respuesta();
                resp.setIdRespuesta(rs.getInt("id_respuesta"));
                resp.setIdComentario(rs.getInt("id_comentario"));
                resp.setIdUsuario(rs.getInt("id_usuario"));
                resp.setContenido(rs.getString("contenido"));
                resp.setFecha(rs.getDate("fecha").toLocalDate());
                resp.setNombreUsuario(rs.getString("nickname"));
                
                respuestas.add(resp);
            }
            
            System.out.println("Obtenidas " + respuestas.size() + 
                             " respuestas del comentario " + idComentario);
            
        } catch (SQLException e) {
            System.err.println("Error al obtener respuestas: " + e.getMessage());
        }
        
        return respuestas;
    }
    
    //Obtiene una respuesta por medio de su id
    public Respuesta obtenerRespuestaPorId(int idRespuesta) {
        String sql = "SELECT r.id_respuesta, r.id_comentario, r.id_usuario, r.contenido, " +
                    "r.fecha, u.nickname " +
                    "FROM respuesta r " +
                    "INNER JOIN usuario u ON r.id_usuario = u.id_usuario " +
                    "WHERE r.id_respuesta = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRespuesta);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Respuesta resp = new Respuesta();
                resp.setIdRespuesta(rs.getInt("id_respuesta"));
                resp.setIdComentario(rs.getInt("id_comentario"));
                resp.setIdUsuario(rs.getInt("id_usuario"));
                resp.setContenido(rs.getString("contenido"));
                resp.setFecha(rs.getDate("fecha").toLocalDate());
                resp.setNombreUsuario(rs.getString("nickname"));
                
                return resp;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener respuesta: " + e.getMessage());
        }
        
        return null;
    }
    
    //Obtiene todas las respuestas que ha hecho un usuario
    public List<Respuesta> obtenerRespuestasPorUsuario(int idUsuario) {
        List<Respuesta> respuestas = new ArrayList<>();
        
        String sql = "SELECT r.id_respuesta, r.id_comentario, r.id_usuario, r.contenido, " +
                    "r.fecha, u.nickname " +
                    "FROM respuesta r " +
                    "INNER JOIN usuario u ON r.id_usuario = u.id_usuario " +
                    "WHERE r.id_usuario = ? " +
                    "ORDER BY r.fecha DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Respuesta resp = new Respuesta();
                resp.setIdRespuesta(rs.getInt("id_respuesta"));
                resp.setIdComentario(rs.getInt("id_comentario"));
                resp.setIdUsuario(rs.getInt("id_usuario"));
                resp.setContenido(rs.getString("contenido"));
                resp.setFecha(rs.getDate("fecha").toLocalDate());
                resp.setNombreUsuario(rs.getString("nickname"));
                
                respuestas.add(resp);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener respuestas del usuario: " + e.getMessage());
        }
        
        return respuestas;
    }
    
    //Cantidad de respuesta en un comentario
    public int contarRespuestas(int idComentario) {
        String sql = "SELECT COUNT(*) FROM respuesta WHERE id_comentario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idComentario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar respuestas: " + e.getMessage());
        }
        
        return 0;
    }
    
    //Actualiza (edita) el contenido de una respuesta
    public boolean actualizarRespuesta(int idRespuesta, String nuevoContenido) {
        String sql = "UPDATE respuesta SET contenido = ? WHERE id_respuesta = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoContenido);
            ps.setInt(2, idRespuesta);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Respuesta actualizada");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar respuesta: " + e.getMessage());
        }
        
        return false;
    }

    
    //Elimina una respuesta 
    public boolean eliminarRespuesta(int idRespuesta) {
        String sql = "DELETE FROM respuesta WHERE id_respuesta = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRespuesta);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Respuesta eliminada: " + idRespuesta);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar respuesta: " + e.getMessage());
        }
        
        return false;
    }
    
    //Elimina todas las respuestas de un comentario
    public boolean eliminarRespuestasPorComentario(int idComentario) {
        String sql = "DELETE FROM respuesta WHERE id_comentario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idComentario);
            
            int filasAfectadas = ps.executeUpdate();
            
            System.out.println("Eliminadas " + filasAfectadas + " respuestas del comentario " + idComentario);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar respuestas por comentario: " + e.getMessage());
        }
        
        return false;
    }
}
