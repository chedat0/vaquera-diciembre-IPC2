/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import com.mycompany.vaqueras_ipc2.modelo.GrupoFamiliar;
import com.mycompany.vaqueras_ipc2.modelo.GrupoMiembro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeffm
 */
public class GrupoFamiliarDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    public GrupoFamiliarDAO() {
        conn = connMySQL.conectar();
    }
    
    //Crea un nuevo grupo familiar
     public boolean crearGrupo(GrupoFamiliar grupo) {
        Connection conn = null;
        
        try {
            conn = connMySQL.conectar();
            conn.setAutoCommit(false);
            
            //Insertar grupo
            String sqlGrupo = "INSERT INTO grupo_familiar (nombre, id_creador) VALUES (?, ?)";
            int idGrupoGenerado;
            
            try (PreparedStatement ps = conn.prepareStatement(sqlGrupo, 
                    Statement.RETURN_GENERATED_KEYS)) {
                
                ps.setString(1, grupo.getNombre());
                ps.setInt(2, grupo.getIdCreador());
                
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas == 0) {
                    conn.rollback();
                    return false;
                }
                
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    idGrupoGenerado = rs.getInt(1);
                    grupo.setIdGrupo(idGrupoGenerado);
                } else {
                    conn.rollback();
                    return false;
                }
            }
            
            // se genera de una vez el rol de "CREADOR"
            String sqlMiembro = "INSERT INTO grupo_miembro (id_grupo, id_usuario, rol) VALUES (?, ?, 'CREADOR')";
            
            try (PreparedStatement ps = conn.prepareStatement(sqlMiembro)) {
                ps.setInt(1, idGrupoGenerado);
                ps.setInt(2, grupo.getIdCreador());
                
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            System.out.println("Grupo creado: " + grupo.getNombre() + " (ID: " + idGrupoGenerado + ")");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al crear grupo: " + e.getMessage());
            e.printStackTrace();
            
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

     //Obtiene un grupo por medio de la id y se muestra la informacion de los miembros
     public GrupoFamiliar obtenerGrupoPorId(int idGrupo) {
        String sql = "SELECT g.id_grupo, g.nombre, g.id_creador, u.nickname as nombre_creador, " +
                    "COUNT(gm.id_usuario) as cantidad_miembros " +
                    "FROM grupo_familiar g " +
                    "INNER JOIN usuario u ON g.id_creador = u.id_usuario " +
                    "LEFT JOIN grupo_miembro gm ON g.id_grupo = gm.id_grupo " +
                    "WHERE g.id_grupo = ? " +
                    "GROUP BY g.id_grupo, g.nombre, g.id_creador, u.nickname";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                GrupoFamiliar grupo = new GrupoFamiliar();
                grupo.setIdGrupo(rs.getInt("id_grupo"));
                grupo.setNombre(rs.getString("nombre"));
                grupo.setIdCreador(rs.getInt("id_creador"));
                grupo.setNombreCreador(rs.getString("nombre_creador"));
                grupo.setCantidadMiembros(rs.getInt("cantidad_miembros"));
                
                System.out.println("Grupo obtenido: " + grupo.getNombre());
                return grupo;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener grupo: " + e.getMessage());
        }
        
        return null;
    }
     
     //Obtiene los grupos a los que pertenece un usuario
     public List<GrupoFamiliar> obtenerGruposPorUsuario(int idUsuario) {
        List<GrupoFamiliar> grupos = new ArrayList<>();
        
        String sql = "SELECT g.id_grupo, g.nombre, g.id_creador, u.nickname as nombre_creador, " +
                    "COUNT(gm2.id_usuario) as cantidad_miembros, gm.rol " +
                    "FROM grupo_familiar g " +
                    "INNER JOIN grupo_miembro gm ON g.id_grupo = gm.id_grupo " +
                    "INNER JOIN usuario u ON g.id_creador = u.id_usuario " +
                    "LEFT JOIN grupo_miembro gm2 ON g.id_grupo = gm2.id_grupo " +
                    "WHERE gm.id_usuario = ? " +
                    "GROUP BY g.id_grupo, g.nombre, g.id_creador, u.nickname, gm.rol " +
                    "ORDER BY g.nombre";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                GrupoFamiliar grupo = new GrupoFamiliar();
                grupo.setIdGrupo(rs.getInt("id_grupo"));
                grupo.setNombre(rs.getString("nombre"));
                grupo.setIdCreador(rs.getInt("id_creador"));
                grupo.setNombreCreador(rs.getString("nombre_creador"));
                grupo.setCantidadMiembros(rs.getInt("cantidad_miembros"));
                
                grupos.add(grupo);
            }
            
            System.out.println("Obtenidos " + grupos.size() + " grupos del usuario " + idUsuario);
            
        } catch (SQLException e) {
            System.err.println("Error al obtener grupos: " + e.getMessage());
        }
        
        return grupos;
    }
     
     //Obtiene los miembros de un grupo
     public List<GrupoMiembro> obtenerMiembrosGrupo(int idGrupo) {
        List<GrupoMiembro> miembros = new ArrayList<>();
        
        String sql = "SELECT gm.id_grupo, gm.id_usuario, gm.rol, u.nickname " +
                    "FROM grupo_miembro gm " +
                    "INNER JOIN usuario u ON gm.id_usuario = u.id_usuario " +
                    "WHERE gm.id_grupo = ? " +
                    "ORDER BY gm.rol DESC, u.nickname";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                GrupoMiembro miembro = new GrupoMiembro();
                miembro.setIdGrupo(rs.getInt("id_grupo"));
                miembro.setIdUsuario(rs.getInt("id_usuario"));
                miembro.setRol(rs.getString("rol"));
                miembro.setNombreUsuario(rs.getString("nickname"));
                
                miembros.add(miembro);
            }
            
            System.out.println("Obtenidos " + miembros.size() + " miembros del grupo " + idGrupo);
            
        } catch (SQLException e) {
            System.err.println("Error al obtener miembros: " + e.getMessage());
        }
        
        return miembros;
    }

     //Si un usuario acepta la invitacion lo agrega a un grupo
     public boolean agregarMiembro(int idGrupo, int idUsuario) {
        String sql = "INSERT INTO grupo_miembro (id_grupo, id_usuario, rol) VALUES (?, ?, 'MIEMBRO')";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idUsuario);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Miembro agregado al grupo: " + idUsuario + " → " + idGrupo);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al agregar miembro: " + e.getMessage());
        }
        
        return false;
    }
     
     //Elimina un miembro de un grupo
     public boolean eliminarMiembro(int idGrupo, int idUsuario) {
        // Verificar que no sea el creador
        GrupoFamiliar grupo = obtenerGrupoPorId(idGrupo);
        
        if (grupo != null && grupo.getIdCreador() == idUsuario) {
            System.err.println("No se puede eliminar al creador del grupo");
            return false;
        }
        
        String sql = "DELETE FROM grupo_miembro WHERE id_grupo = ? AND id_usuario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idUsuario);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Miembro eliminado del grupo: " + idUsuario);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar miembro: " + e.getMessage());
        }
        
        return false;
    }
     
     //Cuenta los miembros de un grupo
     public int contarMiembros(int idGrupo) {
        String sql = "SELECT COUNT(*) FROM grupo_miembro WHERE id_grupo = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar miembros: " + e.getMessage());
        }
        
        return 0;
    }
     
     //verifica si un usuario es miembro de un grupo
     public boolean esMiembro(int idGrupo, int idUsuario) {
        String sql = "SELECT COUNT(*) FROM grupo_miembro WHERE id_grupo = ? AND id_usuario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar membresía: " + e.getMessage());
        }
        
        return false;
    }
     
     //verifica si un usuario es creador de un grupo
     public boolean esCreador(int idGrupo, int idUsuario) {
        String sql = "SELECT COUNT(*) FROM grupo_familiar WHERE id_grupo = ? AND id_creador = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar creador: " + e.getMessage());
        }
        
        return false;
    }
     
     //Actualiza el nombre de un grupo
     public boolean actualizarNombreGrupo(int idGrupo, String nuevoNombre) {
        String sql = "UPDATE grupo_familiar SET nombre = ? WHERE id_grupo = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoNombre);
            ps.setInt(2, idGrupo);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Nombre de grupo actualizado: " + nuevoNombre);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar nombre: " + e.getMessage());
        }
        
        return false;
    }
     
     //El creador puede eliminar un grupo
     public boolean eliminarGrupo(int idGrupo) {
        Connection conn = null;
        
        try {
            conn = connMySQL.conectar();
            conn.setAutoCommit(false);
            
            // Eliminar miembros
            String sqlMiembros = "DELETE FROM grupo_miembro WHERE id_grupo = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlMiembros)) {
                ps.setInt(1, idGrupo);
                ps.executeUpdate();
            }
            
            // Eliminar invitaciones pendientes
            String sqlInvitaciones = "DELETE FROM invitacion_grupo WHERE id_grupo = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlInvitaciones)) {
                ps.setInt(1, idGrupo);
                ps.executeUpdate();
            }
            
            // Eliminar grupo
            String sqlGrupo = "DELETE FROM grupo_familiar WHERE id_grupo = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlGrupo)) {
                ps.setInt(1, idGrupo);
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            System.out.println("Grupo eliminado: " + idGrupo);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar grupo: " + e.getMessage());
            
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
