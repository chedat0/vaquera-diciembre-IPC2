/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.Usuario;
import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import java.sql.*;
import java.time.LocalDate;
/**
 *
 * @author jeffm
 */
public class UsuarioDAO {
    // Buscar usuario por correo (para login)
    public Usuario buscarPorCorreo(String correo) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "SELECT * FROM usuario WHERE correo = ?";
        
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNickname(rs.getString("nickname"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setPassword(rs.getString("password"));
                usuario.setFechaNacimiento(rs.getObject("fecha_nacimiento", LocalDate.class));
                usuario.setTelefono(rs.getString("telefono"));
                usuario.setPais(rs.getString("pais"));
                usuario.setIdRol(rs.getInt("id_rol"));
                return usuario;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    //Verifica si un correo ya existe
    public boolean existeCorreo(String correo){
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "SELECT COUNT(*) FROM usuario WHERE correo = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql))
            {

                stmt.setString(1, correo);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }catch (SQLException e){
                e.printStackTrace();
                }
            return false;
    }
    
    // Crear nuevo usuario (registro)
    public boolean crearUsuario(Usuario usuario) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "INSERT INTO usuario (nickname, correo, password, fecha_nacimiento, " +
                     "telefono, pais, id_rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNickname());
            stmt.setString(2, usuario.getCorreo());
            stmt.setString(3, usuario.getPassword());
            stmt.setDate(4, Date.valueOf(usuario.getFechaNacimiento()));
            stmt.setString(5, usuario.getTelefono());
            stmt.setString(6, usuario.getPais());
            stmt.setInt(7, usuario.getIdRol());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
