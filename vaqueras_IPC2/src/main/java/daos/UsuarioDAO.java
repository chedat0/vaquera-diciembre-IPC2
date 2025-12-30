/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.Usuario;
import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jeffm
 */
public class UsuarioDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;
    
    public UsuarioDAO(){
        conn = connMySQL.conectar();
    }
    //Obtener usuario por id
    public Usuario obtenerUsuarioPorId(int idUsuario) {        
        
        String sql = "SELECT id_usuario, nickname, correo, fecha_nacimiento, telefono, pais, "
                + "id_rol, biblioteca_publica FROM usuario WHERE id_usuario = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNickname(rs.getString("nickname"));
                usuario.setCorreo(rs.getString("correo"));

                java.sql.Date fecha = rs.getDate("fecha_nacimiento");
                if (fecha != null) {
                    usuario.setFechaNacimiento(fecha.toLocalDate());
                }

                usuario.setTelefono(rs.getString("telefono"));
                usuario.setPais(rs.getString("pais"));
                usuario.setIdRol(rs.getInt("id_rol"));
                usuario.setBibliotecaPublica(rs.getBoolean("biblioteca_publica"));

                System.out.println("Usuario obtenido: " + usuario.getNickname());
                return usuario;
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
    
    // Buscar usuario por correo (para login)
    public Usuario buscarPorCorreo(String correo) {
        
        if (correo == null || correo.trim().isEmpty()) {
            System.err.println("Error: correo no puede ser null o vacío");
            return null;
        }
        
        PreparedStatement stmt = null;
        ResultSet rs = null;           
        
        try {
            conn = connMySQL.conectar();
            
            // VALIDACIÓN CRÍTICA: verificar que la conexión no sea null
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión a la BD");
                return null;
            }
            
            String sql = "SELECT * FROM usuario WHERE correo = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, correo);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNickname(rs.getString("nickname"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setPassword(rs.getString("password"));
                
                // Manejo seguro de fecha que puede ser null
                Date fechaBD = rs.getDate("fecha_nacimiento");
                if (fechaBD != null) {
                    usuario.setFechaNacimiento(fechaBD.toLocalDate());
                }
                
                usuario.setTelefono(rs.getString("telefono"));
                usuario.setPais(rs.getString("pais"));
                usuario.setIdRol(rs.getInt("id_rol"));
                return usuario;
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("Error SQL en buscarPorCorreo: " + e.getMessage());
            e.printStackTrace();
            return null;
            
        } finally {
            // Cerrar recursos en orden inverso
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) connMySQL.desconectar(conn);
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }
    
    /**
     * Verifica si un correo ya existe en la BD
     * @param correo Email a verificar
     * @return true si existe, false si no
     */
    public boolean existeCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = connMySQL.conectar();
            
            // VALIDACIÓN CRÍTICA
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión a la BD");
                return false;
            }
            
            String sql = "SELECT COUNT(*) FROM usuario WHERE correo = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, correo);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error SQL en existeCorreo: " + e.getMessage());
            e.printStackTrace();
            return false;
            
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) connMySQL.desconectar(conn);
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }
    
    /**
     * Crea un nuevo usuario en la BD
     * @param usuario Usuario a crear
     * @return true si se creó exitosamente
     */
    public boolean crearUsuario(Usuario usuario) {
        // Validaciones de entrada
        if (usuario == null) {
            System.err.println("Error: usuario no puede ser null");
            return false;
        }
        
        if (usuario.getNickname() == null || usuario.getCorreo() == null || 
            usuario.getPassword() == null) {
            System.err.println("Error: campos obligatorios no pueden ser null");
            return false;
        }
        
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = connMySQL.conectar();
            
            // VALIDACIÓN CRÍTICA
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión a la BD");
                return false;
            }
            
            String sql = "INSERT INTO usuario (nickname, correo, password, fecha_nacimiento, " +
                        "telefono, pais, id_rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario.getNickname());
            stmt.setString(2, usuario.getCorreo());
            stmt.setString(3, usuario.getPassword());
            
            // Manejo seguro de fecha
            if (usuario.getFechaNacimiento() != null) {
                stmt.setDate(4, Date.valueOf(usuario.getFechaNacimiento()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            
            // Manejo seguro de teléfono
            if (usuario.getTelefono() != null) {
                stmt.setString(5, usuario.getTelefono());
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }
            
            stmt.setString(6, usuario.getPais());
            
            // Manejo seguro de rol con valor por defecto
            Integer idRol = usuario.getIdRol();
            stmt.setInt(7, idRol != null ? idRol : 1); // 1 = usuario normal por defecto
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error SQL en crearUsuario: " + e.getMessage());
            e.printStackTrace();
            return false;
            
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) connMySQL.desconectar(conn);
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }
    
    //obtiene listado de jugadores
    public List<Usuario> obtenerTodosJugadores() {
        List<Usuario> jugadores = new ArrayList<>();              
        String sql = "SELECT id_usuario, nickname, correo, fecha_nacimiento, telefono, "
                + "pais, biblioteca_publica FROM usuario WHERE id_rol = 3 ORDER BY nickname";

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
                u.setBibliotecaPublica(rs.getBoolean("biblioteca_publica"));
                u.setIdRol(3);

                jugadores.add(u);
            }

            System.out.println("Obtenidos " + jugadores.size() + " jugadores");

        } catch (SQLException e) {
            System.err.println(" Error al obtener jugadores: " + e.getMessage());
        }

        return jugadores;
    }
    
    //Actualiza jugadores
    public boolean actualizarJugador(Usuario usuario) {
              
        String sql = "UPDATE usuario SET nickname = ?, fecha_nacimiento = ?, telefono = ?, "
                + "pais = ?, biblioteca_publica = ? WHERE id_usuario = ? AND id_rol = 3";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNickname());
            ps.setDate(2, Date.valueOf(usuario.getFechaNacimiento()));
            ps.setString(3, usuario.getTelefono());
            ps.setString(4, usuario.getPais());
            ps.setBoolean(5, usuario.getBibliotecaPublica());
            ps.setInt(6, usuario.getIdUsuario());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Jugador actualizado: " + usuario.getNickname());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar jugador: " + e.getMessage());
        }

        return false;
    }
    
    //Elimina un jugador
    public boolean eliminarJugador(int idUsuario) {
             
        String sql = "DELETE FROM usuario WHERE id_usuario = ? AND id_rol = 3";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Jugador eliminado: " + idUsuario);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar jugador: " + e.getMessage());
        }

        return false;
    }
    
    //Busca jugadores por nickname
    public List<Usuario> buscarJugadoresPorNickname(String nickname) {
        List<Usuario> jugadores = new ArrayList<>();
             
        String sql = "SELECT id_usuario, nickname, correo, fecha_nacimiento, telefono, "
                + "pais, biblioteca_publica FROM usuario "
                + "WHERE id_rol = 3 AND nickname LIKE ? ORDER BY nickname";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nickname + "%");
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
                u.setBibliotecaPublica(rs.getBoolean("biblioteca_publica"));
                u.setIdRol(3);

                jugadores.add(u);
            }

        } catch (SQLException e) {
            System.err.println(" Error al buscar jugadores: " + e.getMessage());
        }

        return jugadores;
    }
    
    //Cantidad de jugadores registrados
    public int contarJugadores() {
               
        String sql = "SELECT COUNT(*) FROM usuario WHERE id_rol = 3";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println(" Error al contar jugadores: " + e.getMessage());
        }

        return 0;
    }
}
