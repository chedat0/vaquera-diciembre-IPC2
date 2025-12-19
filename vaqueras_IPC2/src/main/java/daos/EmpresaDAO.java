/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.Empresa;
import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jeffm
 */
public class EmpresaDAO {
    // Listar todas las empresas
    public List<Empresa> listarTodas() {
        List<Empresa> empresas = new ArrayList<>();
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "SELECT * FROM empresa ORDER BY nombre";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                empresas.add(mapearEmpresa(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return empresas;
    }
    
    // Buscar por ID
    public Empresa buscarPorId(Integer idEmpresa) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "SELECT * FROM empresa WHERE id_empresa = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idEmpresa);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearEmpresa(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Crear empresa
    public boolean crear(Empresa empresa) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "INSERT INTO empresa (nombre, descripcion) VALUES (?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, empresa.getNombre());
            stmt.setString(2, empresa.getDescripcion());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    empresa.setIdEmpresa(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Actualizar empresa
    public boolean actualizar(Empresa empresa) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "UPDATE empresa SET nombre = ?, descripcion = ? WHERE id_empresa = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, empresa.getNombre());
            stmt.setString(2, empresa.getDescripcion());
            stmt.setInt(3, empresa.getIdEmpresa());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Eliminar empresa
    public boolean eliminar(Integer idEmpresa) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "DELETE FROM empresa WHERE id_empresa = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idEmpresa);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Buscar por nombre
    public List<Empresa> buscarPorNombre(String nombre) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        List<Empresa> empresas = new ArrayList<>();
        String sql = "SELECT * FROM empresa WHERE nombre LIKE ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + nombre + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                empresas.add(mapearEmpresa(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return empresas;
    }
    
    // Mapear ResultSet a Empresa
    private Empresa mapearEmpresa(ResultSet rs) throws SQLException {
        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(rs.getInt("id_empresa"));
        empresa.setNombre(rs.getString("nombre"));
        empresa.setDescripcion(rs.getString("descripcion"));
        return empresa;
    }
}
