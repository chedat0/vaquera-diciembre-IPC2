/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import java.math.BigDecimal;
import java.sql.*;
/**
 *
 * @author jeffm
 */
public class ComisionDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    
    public ComisionDAO() {
        conn = connMySQL.conectar();
    }
    
    //Se obtiene la comision global actual
    public double obtenerComisionGlobal() {
        String sql = "SELECT porcentaje FROM comision_global " +
                    "ORDER BY fecha_inicio DESC LIMIT 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                double comision = rs.getDouble("porcentaje");
                System.out.println("Comisión global obtenida: " + comision + "%");
                return comision;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener comisión global: " + e.getMessage());
        }
        
        // Por defecto 15%
        return 15.00;
    }
    
    //Obtiene comision especifica de una empresa,si no tiene entonces retorna un null
    public Double obtenerComisionEmpresa(int idEmpresa) {
        String sql = "SELECT porcentaje FROM comision_empresa WHERE id_empresa = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEmpresa);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                double comision = rs.getDouble("porcentaje");
                System.out.println("Comisión específica empresa " + idEmpresa + ": " + comision + "%");
                return comision;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener comisión de empresa: " + e.getMessage());
        }
        
        return null;
    }
    
    //Asigna la comision global a una empresa en caso de que no tenga especifica
    public double obtenerComisionAplicable(int idEmpresa) {
        Double comisionEspecifica = obtenerComisionEmpresa(idEmpresa);
        
        if (comisionEspecifica != null) {
            System.out.println("Usando comisión específica para empresa " + idEmpresa);
            return comisionEspecifica;
        }
        
        System.out.println("Usando comisión global para empresa " + idEmpresa);
        return obtenerComisionGlobal();
    }
    
    //Calcula ganancias de empresa y plataforma
    public double[] calcularGanancias(double precioJuego, double porcentajeComision) {
        // Ganancia plataforma
        double gananciaPlataforma = (precioJuego * porcentajeComision) /100.00;            
        
        // Ganancia empresa 
        double gananciaEmpresa = precioJuego - gananciaPlataforma;
        
        System.out.println("Precio: $" + precioJuego + 
                         " | Comisión: " + porcentajeComision + "%" +
                         " | Empresa: $" + gananciaEmpresa + 
                         " | Plataforma: $" + gananciaPlataforma);
        
        return new double[]{gananciaEmpresa, gananciaPlataforma};
    }
    
    //Actualiza comision global
    public boolean actualizarComisionGlobal(double porcentaje) {
        String sql = "INSERT INTO comision_global (porcentaje, fecha_inicio) VALUES (?, CURDATE())";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, porcentaje);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Comisión global actualizada a: " + porcentaje + "%");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar comisión global: " + e.getMessage());
        }
        
        return false;
    }
    
    //Establece o cambia la comision especifica de una empresa
    public boolean establecerComisionEmpresa(int idEmpresa, double porcentaje) {
        String sql = "INSERT INTO comision_empresa (id_empresa, porcentaje) " +
                    "VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE porcentaje = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEmpresa);
            ps.setDouble(2, porcentaje);
            ps.setDouble(3, porcentaje);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Comisión empresa " + idEmpresa + " establecida a: " + porcentaje + "%");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al establecer comisión de empresa: " + e.getMessage());
        }
        
        return false;
    }
}
