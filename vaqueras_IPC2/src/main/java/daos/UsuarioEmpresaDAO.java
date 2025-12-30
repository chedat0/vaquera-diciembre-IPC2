/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import com.mycompany.vaqueras_ipc2.modelo.Encriptar;
import com.mycompany.vaqueras_ipc2.modelo.UsuarioEmpresa;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jeffm
 */
public class UsuarioEmpresaDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;    

    public UsuarioEmpresaDAO() {
        conn = connMySQL.conectar();
    }

    //Crea un nuevo usuario de empresa
    public boolean crearUsuarioEmpresa(UsuarioEmpresa usuario) {
        Connection conn = null;
        
        try {
            conn = connMySQL.conectar();
            conn.setAutoCommit(false);
            
            // Valida que el correo no exista
            if (existeCorreo(usuario.getCorreo())) {
                System.err.println("El correo ya existe: " + usuario.getCorreo());
                return false;
            }
            
            // Validar que la empresa existe
            if (!existeEmpresa(usuario.getIdEmpresa())) {
                System.err.println("La empresa no existe: " + usuario.getIdEmpresa());
                return false;
            }
            
            // Encripta la contraseña
            String passwordEncriptado = Encriptar.hashPassword(usuario.getPassword());
            
            // Insertar usuario con rol EMPRESA
            String sqlUsuario = "INSERT INTO usuario (correo, nickname, fecha_nacimiento, " +
                               "password, id_rol) VALUES (?, ?, ?, ?, 2)";
            
            int idUsuarioGenerado;
            
            try (PreparedStatement ps = conn.prepareStatement(sqlUsuario, 
                    Statement.RETURN_GENERATED_KEYS)) {
                
                ps.setString(1, usuario.getCorreo());
                ps.setString(2, usuario.getNombre()); // Guardamos nombre en nickname
                ps.setDate(3, Date.valueOf(usuario.getFechaNacimiento()));
                ps.setString(4, passwordEncriptado);
                
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas == 0) {
                    conn.rollback();
                    return false;
                }
                
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    idUsuarioGenerado = rs.getInt(1);
                    usuario.setIdUsuario(idUsuarioGenerado);
                } else {
                    conn.rollback();
                    return false;
                }
            }
            
            // Vincular usuario con empresa en usuario_empresa
            String sqlVinculo = "INSERT INTO usuario_empresa (id_usuario, id_empresa) VALUES (?, ?)";
            
            try (PreparedStatement ps = conn.prepareStatement(sqlVinculo)) {
                ps.setInt(1, idUsuarioGenerado);
                ps.setInt(2, usuario.getIdEmpresa());
                
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            System.out.println("Usuario de empresa creado: " + usuario.getCorreo() + 
                             " (ID: " + idUsuarioGenerado + ")");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al crear usuario de empresa: " + e.getMessage());
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

    // Obtiene los usuarios de una empresa
    public List<UsuarioEmpresa> obtenerUsuariosPorEmpresa(int idEmpresa) {
        List<UsuarioEmpresa> usuarios = new ArrayList<>();
        
        String sql = "SELECT u.id_usuario, u.correo, u.nickname, u.fecha_nacimiento, " +
                    "ue.id_empresa " +
                    "FROM usuario u " +
                    "INNER JOIN usuario_empresa ue ON u.id_usuario = ue.id_usuario " +
                    "WHERE ue.id_empresa = ? AND u.id_rol = 2";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEmpresa);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                UsuarioEmpresa usuario = new UsuarioEmpresa();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setNombre(rs.getString("nickname"));
                
                Date fecha = rs.getDate("fecha_nacimiento");
                if (fecha != null) {
                    usuario.setFechaNacimiento(fecha.toLocalDate());
                }
                
                usuario.setIdEmpresa(rs.getInt("id_empresa"));
                usuario.setIdRol(2);
                
                usuarios.add(usuario);
            }
            
            System.out.println("Obtenidos " + usuarios.size() + 
                             " usuarios de la empresa " + idEmpresa);
            
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios de empresa: " + e.getMessage());
            e.printStackTrace();
        }
        
        return usuarios;
    }

    //Obtiene usuario de empresa por ID
    public UsuarioEmpresa obtenerUsuarioEmpresaPorId(int idUsuario) {
        String sql = "SELECT u.id_usuario, u.correo, u.nickname, u.fecha_nacimiento, " +
                    "ue.id_empresa " +
                    "FROM usuario u " +
                    "INNER JOIN usuario_empresa ue ON u.id_usuario = ue.id_usuario " +
                    "WHERE u.id_usuario = ? AND u.id_rol = 2";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                UsuarioEmpresa usuario = new UsuarioEmpresa();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setNombre(rs.getString("nickname"));
                
                Date fecha = rs.getDate("fecha_nacimiento");
                if (fecha != null) {
                    usuario.setFechaNacimiento(fecha.toLocalDate());
                }
                
                usuario.setIdEmpresa(rs.getInt("id_empresa"));
                usuario.setIdRol(2);
                
                return usuario;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario de empresa: " + e.getMessage());
        }
        
        return null;
    }

    //Elimina un usuario de una empresa
    public boolean eliminarUsuarioEmpresa(int idUsuario, int idEmpresa) {
        Connection conn = null;
        
        try {
            conn = connMySQL.conectar();
            conn.setAutoCommit(false);
            
            // Validar que el usuario pertenece a la empresa
            if (!perteneceAEmpresa(idUsuario, idEmpresa)) {
                System.err.println("El usuario " + idUsuario + 
                                 " no pertenece a la empresa " + idEmpresa);
                return false;
            }
            
            // Validar que no sea el único usuario de la empresa
            if (contarUsuariosPorEmpresa(idEmpresa) <= 1) {
                System.err.println("No se puede eliminar el único usuario de la empresa");
                return false;
            }
            
            // Eliminar relación usuario_empresa
            String sqlVinculo = "DELETE FROM usuario_empresa WHERE id_usuario = ? AND id_empresa = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(sqlVinculo)) {
                ps.setInt(1, idUsuario);
                ps.setInt(2, idEmpresa);
                ps.executeUpdate();
            }
            
            //  Eliminar usuario
            String sqlUsuario = "DELETE FROM usuario WHERE id_usuario = ? AND id_rol = 2";
            
            try (PreparedStatement ps = conn.prepareStatement(sqlUsuario)) {
                ps.setInt(1, idUsuario);
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            System.out.println("Usuario de empresa eliminado: " + idUsuario);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario de empresa: " + e.getMessage());
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

    //Actualiza un usuario de empresa (menos el correo)
    public boolean actualizarUsuarioEmpresa(UsuarioEmpresa usuario) {
        String sql = "UPDATE usuario SET nickname = ?, fecha_nacimiento = ? " +
                    "WHERE id_usuario = ? AND id_rol = 2";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setDate(2, Date.valueOf(usuario.getFechaNacimiento()));
            ps.setInt(3, usuario.getIdUsuario());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Usuario de empresa actualizado: " + usuario.getIdUsuario());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario de empresa: " + e.getMessage());
        }
        
        return false;
    }

    //Actualiza o cambia la contraseña de un usuario
    public boolean actualizarPasswordUsuarioEmpresa(int idUsuario, String nuevaPassword) {
        String sql = "UPDATE usuario SET password = ? WHERE id_usuario = ? AND id_rol = 2";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String passwordEncriptado = Encriptar.hashPassword(nuevaPassword);
            
            ps.setString(1, passwordEncriptado);
            ps.setInt(2, idUsuario);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("ontraseña actualizada para usuario: " + idUsuario);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar contraseña: " + e.getMessage());
        }
        
        return false;
    }

    //MÉTODOS DE VALIDACIÓN 
    
    //Verifica si un correo ya existe
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

    //Verifica si una empresa existe
    private boolean existeEmpresa(int idEmpresa) {
        String sql = "SELECT COUNT(*) FROM empresa WHERE id_empresa = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEmpresa);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println(" Error al verificar empresa: " + e.getMessage());
        }
        
        return false;
    }

    //Verifica si un usuario pertenece a la empresa
    private boolean perteneceAEmpresa(int idUsuario, int idEmpresa) {
        String sql = "SELECT COUNT(*) FROM usuario_empresa " +
                    "WHERE id_usuario = ? AND id_empresa = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idEmpresa);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println(" Error al verificar pertenencia: " + e.getMessage());
        }
        
        return false;
    }

    //muestra cuantos usuarios tiene una empresa
    private int contarUsuariosPorEmpresa(int idEmpresa) {
        String sql = "SELECT COUNT(*) FROM usuario_empresa WHERE id_empresa = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEmpresa);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar usuarios: " + e.getMessage());
        }
        
        return 0;
    }

    //Muestra la empresa a la que pertenece un usuario
    public int obtenerIdEmpresaPorUsuario(int idUsuario) {
        String sql = "SELECT id_empresa FROM usuario_empresa WHERE id_usuario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_empresa");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener empresa del usuario: " + e.getMessage());
        }
        
        return -1;
    }
}
