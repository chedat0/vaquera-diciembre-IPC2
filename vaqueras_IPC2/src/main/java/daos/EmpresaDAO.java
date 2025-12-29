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
     public List<Empresa> listarTodas() {
         //Listar empresas
        List<Empresa> empresas = new ArrayList<>();
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = connMySQL.conectar();
                        
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión a la BD");
                return empresas; // Retorna lista vacía en lugar de null
            }
            
            String sql = "SELECT * FROM empresa ORDER BY nombre";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Empresa empresa = mapearEmpresa(rs);
                if (empresa != null) {
                    empresas.add(empresa);
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
        
        return empresas;
    }
        
    public Empresa buscarPorId(Integer idEmpresa) {
        if (idEmpresa == null || idEmpresa <= 0) {
            System.err.println("Error: idEmpresa inválido");
            return null;
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
                return null;
            }
            
            String sql = "SELECT * FROM empresa WHERE id_empresa = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idEmpresa);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearEmpresa(rs);
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
       
    public boolean crear(Empresa empresa) {
        // Validaciones de entrada
        if (empresa == null) {
            System.err.println("Error: empresa no puede ser null");
            return false;
        }
        
        if (empresa.getNombre() == null || empresa.getNombre().trim().isEmpty()) {
            System.err.println("Error: nombre de empresa no puede ser null o vacío");
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
            
            String sql = "INSERT INTO empresa (nombre, descripcion) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, empresa.getNombre());
            
            // Manejo seguro de descripción nullable
            if (empresa.getDescripcion() != null) {
                stmt.setString(2, empresa.getDescripcion());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    empresa.setIdEmpresa(rs.getInt(1));
                }
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("✗ Error SQL en crear: " + e.getMessage());
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
        
    public boolean actualizar(Empresa empresa) {
        // Validaciones
        if (empresa == null || empresa.getIdEmpresa() == null) {
            System.err.println("Error: empresa o ID no pueden ser null");
            return false;
        }
        
        if (empresa.getNombre() == null || empresa.getNombre().trim().isEmpty()) {
            System.err.println("Error: nombre de empresa no puede ser null o vacío");
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
            
            String sql = "UPDATE empresa SET nombre = ?, descripcion = ? WHERE id_empresa = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, empresa.getNombre());
            
            // Manejo seguro de descripción null
            if (empresa.getDescripcion() != null) {
                stmt.setString(2, empresa.getDescripcion());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }
            
            stmt.setInt(3, empresa.getIdEmpresa());
            
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
        
    public boolean eliminar(Integer idEmpresa) {
        if (idEmpresa == null || idEmpresa <= 0) {
            System.err.println("Error: idEmpresa inválido");
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
            
            if (tieneUsuarios(idEmpresa)){
                System.err.println("No se puede eliminar, la empresa tiene usuarios asignados");
                return false;
            }
            
            if (tieneJuegos(idEmpresa)){
                System.err.println("No se puede eliminar, la empresa tiene juegos publicados");
            }
            
            String sql = "DELETE FROM empresa WHERE id_empresa = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idEmpresa);
            
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
        
    public List<Empresa> buscarPorNombre(String nombre) {
        List<Empresa> empresas = new ArrayList<>();
        
        if (nombre == null || nombre.trim().isEmpty()) {
            return empresas;
        }
        
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = connMySQL.conectar();
                        
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión a la BD");
                return empresas;
            }
            
            String sql = "SELECT * FROM empresa WHERE nombre LIKE ? ORDER BY nombre";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + nombre + "%");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Empresa empresa = mapearEmpresa(rs);
                if (empresa != null) {
                    empresas.add(empresa);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error SQL en buscarPorNombre: " + e.getMessage());
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
        
        return empresas;
    }
        
    private Empresa mapearEmpresa(ResultSet rs) {
        try {
            if (rs == null) {
                return null;
            }
            
            Empresa empresa = new Empresa();
            empresa.setIdEmpresa(rs.getInt("id_empresa"));
            empresa.setNombre(rs.getString("nombre"));
            empresa.setDescripcion(rs.getString("descripcion")); // Puede ser null
            
            return empresa;
            
        } catch (SQLException e) {
            System.err.println("Error al mapear empresa: " + e.getMessage());
            return null;
        }
    }
    
    private boolean tieneUsuarios (int idEmpresa){
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;        
        
        String sql = " SELECT COUNT(*) FROM usuario_empresa WHERE id_empresa = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, idEmpresa);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()){
                return rs.getInt(1) > 0;                
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar usuarios: " + e.getMessage());
        }
        return false;
    }
    private boolean tieneJuegos (int idEmpresa){
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;        
        
        String sql = " SELECT COUNT(*) FROM juego WHERE id_empresa = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, idEmpresa);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()){
                return rs.getInt(1) > 0;                
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar juegos: " + e.getMessage());
        }
        return false;
    }
}
