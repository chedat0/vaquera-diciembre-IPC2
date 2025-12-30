/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import com.mycompany.vaqueras_ipc2.modelo.InvitacionGrupo;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jeffm
 */
public class InvitacionGrupoDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;
    
    public InvitacionGrupoDAO() {
        conn = connMySQL.conectar();
    }
    
    //Crea una invitacion a un grupo
    public boolean crearInvitacion(InvitacionGrupo invitacion) {
        String sql = "INSERT INTO invitacion_grupo (id_grupo, id_usuario_invitado, " +
                    "id_usuario_invitador, estado, fecha_invitacion) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, invitacion.getIdGrupo());
            ps.setInt(2, invitacion.getIdUsuarioInvitado());
            ps.setInt(3, invitacion.getIdUsuarioInvitador());
            ps.setString(4, invitacion.getEstado().name());
            ps.setDate(5, Date.valueOf(invitacion.getFechaInvitacion()));
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    invitacion.setIdInvitacion(rs.getInt(1));
                }
                
                System.out.println("Invitación creada: Usuario " + invitacion.getIdUsuarioInvitado() + 
                                 " invitado al grupo " + invitacion.getIdGrupo());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al crear invitación: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    //Obtiene las invitaciones pendientes de un usuario
    public List<InvitacionGrupo> obtenerInvitacionesPendientes(int idUsuario) {
        List<InvitacionGrupo> invitaciones = new ArrayList<>();
        
        String sql = "SELECT i.id_invitacion, i.id_grupo, i.id_usuario_invitado, " +
                    "i.id_usuario_invitador, i.estado, i.fecha_invitacion, " +
                    "g.nombre as nombre_grupo, u.nickname as nombre_invitador " +
                    "FROM invitacion_grupo i " +
                    "INNER JOIN grupo_familiar g ON i.id_grupo = g.id_grupo " +
                    "INNER JOIN usuario u ON i.id_usuario_invitador = u.id_usuario " +
                    "WHERE i.id_usuario_invitado = ? AND i.estado = 'PENDIENTE' " +
                    "ORDER BY i.fecha_invitacion DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                InvitacionGrupo inv = new InvitacionGrupo();
                inv.setIdInvitacion(rs.getInt("id_invitacion"));
                inv.setIdGrupo(rs.getInt("id_grupo"));
                inv.setIdUsuarioInvitado(rs.getInt("id_usuario_invitado"));
                inv.setIdUsuarioInvitador(rs.getInt("id_usuario_invitador"));
                inv.setEstado(InvitacionGrupo.EstadoInvitacion.valueOf(rs.getString("estado")));
                inv.setFechaInvitacion(rs.getDate("fecha_invitacion").toLocalDate());
                inv.setNombreGrupo(rs.getString("nombre_grupo"));
                inv.setNombreInvitador(rs.getString("nombre_invitador"));
                
                invitaciones.add(inv);
            }
            
            System.out.println("Obtenidas " + invitaciones.size() + 
                             " invitaciones pendientes del usuario " + idUsuario);
            
        } catch (SQLException e) {
            System.err.println("Error al obtener invitaciones pendientes: " + e.getMessage());
        }
        
        return invitaciones;
    }
    
    //Obtiene todas las invitaciones de un usuario
    public List<InvitacionGrupo> obtenerTodasInvitaciones(int idUsuario) {
        List<InvitacionGrupo> invitaciones = new ArrayList<>();
        
        String sql = "SELECT i.id_invitacion, i.id_grupo, i.id_usuario_invitado, " +
                    "i.id_usuario_invitador, i.estado, i.fecha_invitacion, " +
                    "g.nombre as nombre_grupo, u.nickname as nombre_invitador " +
                    "FROM invitacion_grupo i " +
                    "INNER JOIN grupo_familiar g ON i.id_grupo = g.id_grupo " +
                    "INNER JOIN usuario u ON i.id_usuario_invitador = u.id_usuario " +
                    "WHERE i.id_usuario_invitado = ? " +
                    "ORDER BY i.fecha_invitacion DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                InvitacionGrupo inv = new InvitacionGrupo();
                inv.setIdInvitacion(rs.getInt("id_invitacion"));
                inv.setIdGrupo(rs.getInt("id_grupo"));
                inv.setIdUsuarioInvitado(rs.getInt("id_usuario_invitado"));
                inv.setIdUsuarioInvitador(rs.getInt("id_usuario_invitador"));
                inv.setEstado(InvitacionGrupo.EstadoInvitacion.valueOf(rs.getString("estado")));
                inv.setFechaInvitacion(rs.getDate("fecha_invitacion").toLocalDate());
                inv.setNombreGrupo(rs.getString("nombre_grupo"));
                inv.setNombreInvitador(rs.getString("nombre_invitador"));
                
                invitaciones.add(inv);
            }
            
        } catch (SQLException e) {
            System.err.println(" Error al obtener invitaciones: " + e.getMessage());
        }
        
        return invitaciones;
    }
    
    //Obtiene una invitacion por id
    public InvitacionGrupo obtenerInvitacionPorId(int idInvitacion) {
        String sql = "SELECT i.id_invitacion, i.id_grupo, i.id_usuario_invitado, " +
                    "i.id_usuario_invitador, i.estado, i.fecha_invitacion, " +
                    "g.nombre as nombre_grupo, u.nickname as nombre_invitador, " +
                    "u2.nickname as nombre_invitado " +
                    "FROM invitacion_grupo i " +
                    "INNER JOIN grupo_familiar g ON i.id_grupo = g.id_grupo " +
                    "INNER JOIN usuario u ON i.id_usuario_invitador = u.id_usuario " +
                    "INNER JOIN usuario u2 ON i.id_usuario_invitado = u2.id_usuario " +
                    "WHERE i.id_invitacion = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idInvitacion);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                InvitacionGrupo inv = new InvitacionGrupo();
                inv.setIdInvitacion(rs.getInt("id_invitacion"));
                inv.setIdGrupo(rs.getInt("id_grupo"));
                inv.setIdUsuarioInvitado(rs.getInt("id_usuario_invitado"));
                inv.setIdUsuarioInvitador(rs.getInt("id_usuario_invitador"));
                inv.setEstado(InvitacionGrupo.EstadoInvitacion.valueOf(rs.getString("estado")));
                inv.setFechaInvitacion(rs.getDate("fecha_invitacion").toLocalDate());
                inv.setNombreGrupo(rs.getString("nombre_grupo"));
                inv.setNombreInvitador(rs.getString("nombre_invitador"));
                inv.setNombreInvitado(rs.getString("nombre_invitado"));
                
                return inv;
            }
            
        } catch (SQLException e) {
            System.err.println(" Error al obtener invitación: " + e.getMessage());
        }
        
        return null;
    }

    //Actualiza el estado de una invitacion
    public boolean actualizarEstadoInvitacion(int idInvitacion, 
                                             InvitacionGrupo.EstadoInvitacion nuevoEstado) {
        String sql = "UPDATE invitacion_grupo SET estado = ? WHERE id_invitacion = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.name());
            ps.setInt(2, idInvitacion);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Estado de invitación actualizado: " + nuevoEstado);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado de invitación: " + e.getMessage());
        }
        
        return false;
    }
    
    //Verifica si existe una invitacion pendiente de un usuario a algun grupo
    public boolean tieneInvitacionPendiente(int idGrupo, int idUsuario) {
        String sql = "SELECT COUNT(*) FROM invitacion_grupo " +
                    "WHERE id_grupo = ? AND id_usuario_invitado = ? AND estado = 'PENDIENTE'";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar invitación pendiente: " + e.getMessage());
        }
        
        return false;
    }
    
    //Cancela una invitacion 
    public boolean eliminarInvitacion(int idInvitacion) {
        String sql = "DELETE FROM invitacion_grupo WHERE id_invitacion = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idInvitacion);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Invitación eliminada: " + idInvitacion);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar invitación: " + e.getMessage());
        }
        
        return false;
    }
    
    //Obtiene las invitaciones enviadas por un usuario 
     public List<InvitacionGrupo> obtenerInvitacionesEnviadas(int idUsuario) {
        List<InvitacionGrupo> invitaciones = new ArrayList<>();
        
        String sql = "SELECT i.id_invitacion, i.id_grupo, i.id_usuario_invitado, " +
                    "i.id_usuario_invitador, i.estado, i.fecha_invitacion, " +
                    "g.nombre as nombre_grupo, u.nickname as nombre_invitado " +
                    "FROM invitacion_grupo i " +
                    "INNER JOIN grupo_familiar g ON i.id_grupo = g.id_grupo " +
                    "INNER JOIN usuario u ON i.id_usuario_invitado = u.id_usuario " +
                    "WHERE i.id_usuario_invitador = ? " +
                    "ORDER BY i.fecha_invitacion DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                InvitacionGrupo inv = new InvitacionGrupo();
                inv.setIdInvitacion(rs.getInt("id_invitacion"));
                inv.setIdGrupo(rs.getInt("id_grupo"));
                inv.setIdUsuarioInvitado(rs.getInt("id_usuario_invitado"));
                inv.setIdUsuarioInvitador(rs.getInt("id_usuario_invitador"));
                inv.setEstado(InvitacionGrupo.EstadoInvitacion.valueOf(rs.getString("estado")));
                inv.setFechaInvitacion(rs.getDate("fecha_invitacion").toLocalDate());
                inv.setNombreGrupo(rs.getString("nombre_grupo"));
                inv.setNombreInvitado(rs.getString("nombre_invitado"));
                
                invitaciones.add(inv);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener invitaciones enviadas: " + e.getMessage());
        }
        
        return invitaciones;
    }

}
