/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.Comentario;
import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jeffm
 */
public class ComentarioDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;   
    
    public ComentarioDAO() {
        conn = connMySQL.conectar();
    }
    
    //Crea un nuevo comentario
    public boolean crearComentario(Comentario comentario) {
        String sql = "INSERT INTO comentario (id_juego, id_usuario, contenido, " +
                    "calificacion, fecha, visible) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, comentario.getIdJuego());
            ps.setInt(2, comentario.getIdUsuario());
            ps.setString(3, comentario.getContenido());
            ps.setInt(4, comentario.getCalificacion());
            ps.setDate(5, Date.valueOf(comentario.getFecha()));
            ps.setBoolean(6, comentario.isVisible());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    comentario.setIdComentario(rs.getInt(1));
                }
                
                System.out.println("Comentario creado: ID " + comentario.getIdComentario() + 
                                 " | Calificación: " + comentario.getCalificacion() + " estrellas");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al crear comentario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    //Obtiene los comentarios de un juego
    public List<Comentario> obtenerComentariosPorJuego(int idJuego, boolean soloVisibles) {
        List<Comentario> comentarios = new ArrayList<>();
        
        String sql = "SELECT c.id_comentario, c.id_juego, c.id_usuario, c.contenido, " +
                    "c.calificacion, c.fecha, c.visible, u.nickname, " +
                    "COUNT(r.id_respuesta) as cantidad_respuestas " +
                    "FROM comentario c " +
                    "INNER JOIN usuario u ON c.id_usuario = u.id_usuario " +
                    "LEFT JOIN respuesta r ON c.id_comentario = r.id_comentario " +
                    "WHERE c.id_juego = ? " +
                    (soloVisibles ? "AND c.visible = true " : "") +
                    "GROUP BY c.id_comentario, c.id_juego, c.id_usuario, c.contenido, " +
                    "c.calificacion, c.fecha, c.visible, u.nickname " +
                    "ORDER BY c.fecha DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Comentario c = new Comentario();
                c.setIdComentario(rs.getInt("id_comentario"));
                c.setIdJuego(rs.getInt("id_juego"));
                c.setIdUsuario(rs.getInt("id_usuario"));
                c.setContenido(rs.getString("contenido"));
                c.setCalificacion(rs.getInt("calificacion"));
                c.setFecha(rs.getDate("fecha").toLocalDate());
                c.setVisible(rs.getBoolean("visible"));
                c.setNombreUsuario(rs.getString("nickname"));
                c.setCantidadRespuestas(rs.getInt("cantidad_respuestas"));
                
                comentarios.add(c);
            }
            
            System.out.println("Obtenidos " + comentarios.size() + 
                             " comentarios del juego " + idJuego);
            
        } catch (SQLException e) {
            System.err.println(" Error al obtener comentarios: " + e.getMessage());
        }
        
        return comentarios;
    }
    
    //Obtiene un comentario por medio de id
    public Comentario obtenerComentarioPorId(int idComentario) {
        String sql = "SELECT c.id_comentario, c.id_juego, c.id_usuario, c.contenido, " +
                    "c.calificacion, c.fecha, c.visible, u.nickname, j.titulo " +
                    "FROM comentario c " +
                    "INNER JOIN usuario u ON c.id_usuario = u.id_usuario " +
                    "INNER JOIN juego j ON c.id_juego = j.id_juego " +
                    "WHERE c.id_comentario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idComentario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Comentario c = new Comentario();
                c.setIdComentario(rs.getInt("id_comentario"));
                c.setIdJuego(rs.getInt("id_juego"));
                c.setIdUsuario(rs.getInt("id_usuario"));
                c.setContenido(rs.getString("contenido"));
                c.setCalificacion(rs.getInt("calificacion"));
                c.setFecha(rs.getDate("fecha").toLocalDate());
                c.setVisible(rs.getBoolean("visible"));
                c.setNombreUsuario(rs.getString("nickname"));
                c.setTituloJuego(rs.getString("titulo"));
                
                return c;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener comentario: " + e.getMessage());
        }
        
        return null;
    }
    
    //Obtiene los comentarios de un usuario
    public List<Comentario> obtenerComentariosPorUsuario(int idUsuario) {
        List<Comentario> comentarios = new ArrayList<>();
        
        String sql = "SELECT c.id_comentario, c.id_juego, c.id_usuario, c.contenido, " +
                    "c.calificacion, c.fecha, c.visible, j.titulo " +
                    "FROM comentario c " +
                    "INNER JOIN juego j ON c.id_juego = j.id_juego " +
                    "WHERE c.id_usuario = ? " +
                    "ORDER BY c.fecha DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Comentario c = new Comentario();
                c.setIdComentario(rs.getInt("id_comentario"));
                c.setIdJuego(rs.getInt("id_juego"));
                c.setIdUsuario(rs.getInt("id_usuario"));
                c.setContenido(rs.getString("contenido"));
                c.setCalificacion(rs.getInt("calificacion"));
                c.setFecha(rs.getDate("fecha").toLocalDate());
                c.setVisible(rs.getBoolean("visible"));
                c.setTituloJuego(rs.getString("titulo"));
                
                comentarios.add(c);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener comentarios del usuario: " + e.getMessage());
        }
        
        return comentarios;
    }
    
    //Calcula la calificacion promedio de un juego
    public double calcularPromedioCalificacion(int idJuego) {
        String sql = "SELECT AVG(calificacion) as promedio FROM comentario WHERE id_juego = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                double promedio = rs.getDouble("promedio");
                System.out.println("Calificación promedio del juego " + idJuego + ": " + 
                                 String.format("%.2f", promedio) + " estrellas");
                return promedio;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al calcular promedio: " + e.getMessage());
        }
        
        return 0.0;
    }
    
    //Cantidad de comentarios que tiene un juego
    public int contarComentarios(int idJuego) {
        String sql = "SELECT COUNT(*) FROM comentario WHERE id_juego = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar comentarios: " + e.getMessage());
        }
        
        return 0;
    }
    
    //Actualiza la visibilidad de un solo comentario
    public boolean actualizarVisibilidad(int idComentario, boolean visible) {
        String sql = "UPDATE comentario SET visible = ? WHERE id_comentario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, visible);
            ps.setInt(2, idComentario);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Visibilidad actualizada: " + 
                                 (visible ? "VISIBLE" : "OCULTO"));
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar visibilidad: " + e.getMessage());
        }
        
        return false;
    }

    //Actualiza la visibilidad de todos los comentarios de un juego
    public boolean actualizarVisibilidadPorJuego(int idJuego, boolean visible) {
        String sql = "UPDATE comentario SET visible = ? WHERE id_juego = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, visible);
            ps.setInt(2, idJuego);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Visibilidad actualizada para " + filasAfectadas + 
                                 " comentarios del juego " + idJuego);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar visibilidad por juego: " + e.getMessage());
        }
        
        return false;
    }
    
    //Actualiza (edita) el contenido de un comentario
    public boolean actualizarComentario(int idComentario, String nuevoContenido) {
        String sql = "UPDATE comentario SET contenido = ? WHERE id_comentario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoContenido);
            ps.setInt(2, idComentario);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Comentario actualizado");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar comentario: " + e.getMessage());
        }
        
        return false;
    }
    
    //Elimina un comentario
    public boolean eliminarComentario(int idComentario) {
        String sql = "DELETE FROM comentario WHERE id_comentario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idComentario);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Comentario eliminado: " + idComentario);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar comentario: " + e.getMessage());
        }
        
        return false;
    }
    
    //Obtiene los comentarios con mejor calificacion
    public List<Comentario> obtenerMejoresComentarios(int idJuego, int limite) {
        List<Comentario> comentarios = new ArrayList<>();
        
        String sql = "SELECT c.id_comentario, c.id_juego, c.id_usuario, c.contenido, " +
                    "c.calificacion, c.fecha, c.visible, u.nickname, " +
                    "COUNT(r.id_respuesta) as cantidad_respuestas " +
                    "FROM comentario c " +
                    "INNER JOIN usuario u ON c.id_usuario = u.id_usuario " +
                    "LEFT JOIN respuesta r ON c.id_comentario = r.id_comentario " +
                    "WHERE c.id_juego = ? AND c.visible = true " +
                    "GROUP BY c.id_comentario, c.id_juego, c.id_usuario, c.contenido, " +
                    "c.calificacion, c.fecha, c.visible, u.nickname " +
                    "ORDER BY c.calificacion DESC, cantidad_respuestas DESC " +
                    "LIMIT ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            ps.setInt(2, limite);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Comentario c = new Comentario();
                c.setIdComentario(rs.getInt("id_comentario"));
                c.setIdJuego(rs.getInt("id_juego"));
                c.setIdUsuario(rs.getInt("id_usuario"));
                c.setContenido(rs.getString("contenido"));
                c.setCalificacion(rs.getInt("calificacion"));
                c.setFecha(rs.getDate("fecha").toLocalDate());
                c.setVisible(rs.getBoolean("visible"));
                c.setNombreUsuario(rs.getString("nickname"));
                c.setCantidadRespuestas(rs.getInt("cantidad_respuestas"));
                
                comentarios.add(c);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener mejores comentarios: " + e.getMessage());
        }
        
        return comentarios;
    }
}
