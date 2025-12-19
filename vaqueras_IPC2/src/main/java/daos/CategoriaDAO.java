/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.Categoria;
import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jeffm
 */
public class CategoriaDAO {
    // Listar todas las categorías
    public List<Categoria> listarTodas() {
        List<Categoria> categorias = new ArrayList<>();
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = connMySQL.conectar();
                        
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión a la BD");
                return categorias;
            }
            
            String sql = "SELECT * FROM categoria ORDER BY nombre";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Categoria categoria = mapearCategoria(rs);
                if (categoria != null) {
                    categorias.add(categoria);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error SQL en listarTodas: " + e.getMessage());
            e.printStackTrace();
            
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) connMySQL.desconectar(conn);
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        
        return categorias;
    }
    
    //Categorias activas
    public List<Categoria> listarActivas() {
        List<Categoria> categorias = new ArrayList<>();
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = connMySQL.conectar();
            
            // VALIDACIÓN CRÍTICA
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión a la BD");
                return categorias;
            }
            
            String sql = "SELECT * FROM categoria WHERE activado = true ORDER BY nombre";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Categoria categoria = mapearCategoria(rs);
                if (categoria != null) {
                    categorias.add(categoria);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error SQL en listarActivas: " + e.getMessage());
            e.printStackTrace();
            
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) connMySQL.desconectar(conn);
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        
        return categorias;
    }
    
    //Busca categoria por ID
    public Categoria buscarPorId(Integer idCategoria) {
        if (idCategoria == null || idCategoria <= 0) {
            System.err.println("Error: idCategoria inválido");
            return null;
        }
        
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = connMySQL.conectar();
                        
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión a la BD");
                return null;
            }
            
            String sql = "SELECT * FROM categoria WHERE id_categoria = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idCategoria);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearCategoria(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("Error SQL en buscarPorId: " + e.getMessage());
            e.printStackTrace();
            return null;
            
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
    
    //Crea categoria
    public boolean crear(Categoria categoria) {
        // Validaciones
        if (categoria == null) {
            System.err.println("Error: categoria no puede ser null");
            return false;
        }
        
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            System.err.println("Error: nombre de categoría no puede ser null o vacío");
            return false;
        }
        
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = connMySQL.conectar();
                        
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión a la BD");
                return false;
            }
            
            String sql = "INSERT INTO categoria (nombre, activado) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, categoria.getNombre());
            
            // Manejo seguro de activado con valor por defecto
            Boolean activado = categoria.getActivado();
            stmt.setBoolean(2, activado != null ? activado : true);
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    categoria.setIdCategoria(rs.getInt(1));
                }
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error SQL en crear: " + e.getMessage());
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
    
    //Actualiza categoria
    public boolean actualizar(Categoria categoria) {
        // Validaciones
        if (categoria == null || categoria.getIdCategoria() == null) {
            System.err.println("Error: categoria o ID no pueden ser null");
            return false;
        }
        
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            System.err.println("Error: nombre no puede ser null o vacío");
            return false;
        }
        
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = connMySQL.conectar();
                        
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión a la BD");
                return false;
            }
            
            String sql = "UPDATE categoria SET nombre = ?, activado = ? WHERE id_categoria = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, categoria.getNombre());
            
            // Manejo seguro de activado
            Boolean activado = categoria.getActivado();
            stmt.setBoolean(2, activado != null ? activado : true);
            
            stmt.setInt(3, categoria.getIdCategoria());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error SQL en actualizar: " + e.getMessage());
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
    
    //Elimina categoria
    public boolean eliminar(Integer idCategoria) {
        if (idCategoria == null || idCategoria <= 0) {
            System.err.println("Error: idCategoria inválido");
            return false;
        }
        
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = connMySQL.conectar();
                        
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión a la BD");
                return false;
            }
            
            String sql = "DELETE FROM categoria WHERE id_categoria = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idCategoria);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error SQL en eliminar: " + e.getMessage());
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
    
    //Verifica si ya hay una categoria con ese nombre
    public boolean existeNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = connMySQL.conectar();
                       
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión a la BD");
                return false;
            }
            
            String sql = "SELECT COUNT(*) FROM categoria WHERE nombre = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error SQL en existeNombre: " + e.getMessage());
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
    
    //Map de Result set
    private Categoria mapearCategoria(ResultSet rs) {
        try {
            if (rs == null) {
                return null;
            }
            
            Categoria categoria = new Categoria();
            categoria.setIdCategoria(rs.getInt("id_categoria"));
            categoria.setNombre(rs.getString("nombre"));
            
            // Manejo seguro de boolean
            boolean activado = rs.getBoolean("activado");
            categoria.setActivado(rs.wasNull() ? true : activado);
            
            return categoria;
            
        } catch (SQLException e) {
            System.err.println("Error al mapear categoría: " + e.getMessage());
            return null;
        }
    }
}
