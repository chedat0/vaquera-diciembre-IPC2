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
        Connection conn = connMySQL.conectar();
        String sql = "SELECT * FROM categoria ORDER BY nombre";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categorias.add(mapearCategoria(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return categorias;
    }
    
    // Listar solo categorías activas
    public List<Categoria> listarActivas() {
        List<Categoria> categorias = new ArrayList<>();
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "SELECT * FROM categoria WHERE activado = true ORDER BY nombre";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categorias.add(mapearCategoria(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return categorias;
    }
    
    // Buscar por ID
    public Categoria buscarPorId(Integer idCategoria) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "SELECT * FROM categoria WHERE id_categoria = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCategoria);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearCategoria(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Crear categoría
    public boolean crear(Categoria categoria) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "INSERT INTO categoria (nombre, activado) VALUES (?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, categoria.getNombre());
            stmt.setBoolean(2, categoria.getActivado() != null ? categoria.getActivado() : true);
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    categoria.setIdCategoria(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Actualizar categoría
    public boolean actualizar(Categoria categoria) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "UPDATE categoria SET nombre = ?, activado = ? WHERE id_categoria = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria.getNombre());
            stmt.setBoolean(2, categoria.getActivado());
            stmt.setInt(3, categoria.getIdCategoria());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Eliminar categoría
    public boolean eliminar(Integer idCategoria) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "DELETE FROM categoria WHERE id_categoria = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCategoria);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Verificar si existe por nombre
    public boolean existeNombre(String nombre) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "SELECT COUNT(*) FROM categoria WHERE nombre = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Mapear ResultSet a Categoria
    private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(rs.getInt("id_categoria"));
        categoria.setNombre(rs.getString("nombre"));
        categoria.setActivado(rs.getBoolean("activado"));
        return categoria;
    }
}
