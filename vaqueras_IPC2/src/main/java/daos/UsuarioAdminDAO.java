/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import com.mycompany.vaqueras_ipc2.modelo.Usuario;
import com.mycompany.vaqueras_ipc2.modelo.Encriptar;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jeffm
 */
public class UsuarioAdminDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;    

    public UsuarioAdminDAO() {
        conn = connMySQL.conectar();
    }
    
    //Crea un nuevo admin/moderador
    public boolean crearUsuarioAdmin(Usuario usuario) {
        // Validar que el correo no exista
        if (existeCorreo(usuario.getCorreo())) {
            System.err.println("El correo ya está registrado: " + usuario.getCorreo());
            return false;
        }
        
        String sql = "INSERT INTO usuario (nickname, correo, password, fecha_nacimiento, " +
                    "telefono, pais, id_rol) VALUES (?, ?, ?, ?, ?, ?, 1)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, usuario.getNickname());
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, Encriptar.hashPassword(usuario.getPassword()));
            ps.setDate(4, Date.valueOf(usuario.getFechaNacimiento()));
            ps.setString(5, usuario.getTelefono());
            ps.setString(6, usuario.getPais());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    usuario.setIdUsuario(rs.getInt(1));
                }
                
                System.out.println("Usuario administrador creado: " + usuario.getNickname() + 
                                 " (ID: " + usuario.getIdUsuario() + ")");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al crear usuario admin: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    //Obtiene todos los usuarios administradores
    public List<Usuario> obtenerTodosAdmins() {
        List<Usuario> admins = new ArrayList<>();
        
        String sql = "SELECT id_usuario, nickname, correo, fecha_nacimiento, telefono, pais " +
                    "FROM usuario " +
                    "WHERE id_rol = 1 " +
                    "ORDER BY nickname";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNickname(rs.getString("nickname"));
                u.setCorreo(rs.getString("correo"));
                
                Date fecha = rs.getDate("fecha_nacimiento");
                if (fecha != null) {
                    u.setFechaNacimiento(fecha.toLocalDate());
                }
                
                u.setTelefono(rs.getString("telefono"));
                u.setPais(rs.getString("pais"));
                u.setIdRol(1);
                
                admins.add(u);
            }
            
            System.out.println("Obtenidos " + admins.size() + " usuarios administradores");
            
        } catch (SQLException e) {
            System.err.println(" Error al obtener admins: " + e.getMessage());
        }
        
        return admins;
    }
    
    //Obtiene usuario admin por medio de id
    public Usuario obtenerAdminPorId(int idUsuario) {
        String sql = "SELECT id_usuario, nickname, correo, fecha_nacimiento, telefono, pais " +
                    "FROM usuario " +
                    "WHERE id_usuario = ? AND id_rol = 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNickname(rs.getString("nickname"));
                u.setCorreo(rs.getString("correo"));
                
                Date fecha = rs.getDate("fecha_nacimiento");
                if (fecha != null) {
                    u.setFechaNacimiento(fecha.toLocalDate());
                }
                
                u.setTelefono(rs.getString("telefono"));
                u.setPais(rs.getString("pais"));
                u.setIdRol(1);
                
                return u;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener admin: " + e.getMessage());
        }
        
        return null;
    }
    
    //Actualiza un usuario admin    
    public boolean actualizarAdmin(Usuario usuario) {
        String sql = "UPDATE usuario SET nickname = ?, fecha_nacimiento = ?, " +
                    "telefono = ?, pais = ? WHERE id_usuario = ? AND id_rol = 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNickname());
            ps.setDate(2, Date.valueOf(usuario.getFechaNacimiento()));
            ps.setString(3, usuario.getTelefono());
            ps.setString(4, usuario.getPais());
            ps.setInt(5, usuario.getIdUsuario());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Usuario admin actualizado: " + usuario.getNickname());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar admin: " + e.getMessage());
        }
        
        return false;
    }
    
    //Actualiza la contraseña de un usuario admin
    public boolean actualizarPasswordAdmin(int idUsuario, String nuevaPassword) {
        String sql = "UPDATE usuario SET password = ? WHERE id_usuario = ? AND id_rol = 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Encriptar.hashPassword(nuevaPassword));
            ps.setInt(2, idUsuario);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Contraseña de admin actualizada");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar password admin: " + e.getMessage());
        }
        
        return false;
    }
    
    //Elimina un usuario administrador
    public boolean eliminarAdmin(int idUsuario) {
        // Validar que no sea el único admin
        int totalAdmins = contarAdmins();
        
        if (totalAdmins <= 1) {
            System.err.println("No se puede eliminar el último administrador del sistema");
            return false;
        }
        
        String sql = "DELETE FROM usuario WHERE id_usuario = ? AND id_rol = 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println(" Usuario admin eliminado: " + idUsuario);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar admin: " + e.getMessage());
        }
        
        return false;
    }
    
    //Cantidad de administradores que hay en el sistema
    private int contarAdmins() {
        String sql = "SELECT COUNT(*) FROM usuario WHERE id_rol = 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar admins: " + e.getMessage());
        }
        
        return 0;
    }
    
    //Verifica el correo
    private boolean existeCorreo(String correo) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE correo = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar correo: " + e.getMessage());
        }
        
        return false;
    }
}
