/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author jeffm
 */
public class ComisionDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;    
    
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
        
        // Validar que no sea mayor a la comisión global
        double comisionGlobal = obtenerComisionGlobal();

        if (porcentaje > comisionGlobal) {
            System.err.println("La comisión de empresa (" + porcentaje
                    + "%) no puede ser mayor a la global (" + comisionGlobal + "%)");
            return false;
        }
        
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
    
    //Obtiene las empresas con comision especifica
    public List<Map<String, Object>> obtenerEmpresasConComisionEspecifica() {
        List<Map<String, Object>> empresas = new ArrayList<>();

        String sql = "SELECT ce.id_empresa, e.nombre, ce.porcentaje "
                + "FROM comision_empresa ce "
                + "INNER JOIN empresa e ON ce.id_empresa = e.id_empresa "
                + "ORDER BY e.nombre";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> empresa = new HashMap<>();
                empresa.put("idEmpresa", rs.getInt("id_empresa"));
                empresa.put("nombreEmpresa", rs.getString("nombre"));
                empresa.put("porcentaje", rs.getDouble("porcentaje"));

                empresas.add(empresa);
            }

            System.out.println("Obtenidas " + empresas.size()
                    + " empresas con comisión específica");

        } catch (SQLException e) {
            System.err.println("Error al obtener empresas con comisión: " + e.getMessage());
        }

        return empresas;
    }
    
    //Elimina comision especifica de una empresa y vuelve a usar la comision global
    public boolean eliminarComisionEmpresa(int idEmpresa) {
        String sql = "DELETE FROM comision_empresa WHERE id_empresa = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEmpresa);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Comisión específica eliminada. Empresa usará comisión global");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar comisión de empresa: " + e.getMessage());
        }

        return false;
    }
    
    //Actualiza comision global y ajusta a las empresas que la exceden
    
    public boolean actualizarComisionGlobalConAjuste(double nuevoPorcentaje) {
        Connection conn = null;

        try {
            conn = connMySQL.conectar();
            conn.setAutoCommit(false);

            double comisionActual = obtenerComisionGlobal();

            // Insertar nueva comisión global
            String sqlGlobal = "INSERT INTO comision_global (porcentaje, fecha_inicio) VALUES (?, CURDATE())";

            try (PreparedStatement ps = conn.prepareStatement(sqlGlobal)) {
                ps.setDouble(1, nuevoPorcentaje);
                ps.executeUpdate();
            }

            // Si la nueva comisión es MENOR, ajustar empresas afectadas
            if (nuevoPorcentaje < comisionActual) {
                System.out.println(" Comisión global reducida. Ajustando empresas...");

                String sqlAjuste = "UPDATE comision_empresa SET porcentaje = ? WHERE porcentaje > ?";

                try (PreparedStatement ps = conn.prepareStatement(sqlAjuste)) {
                    ps.setDouble(1, nuevoPorcentaje);
                    ps.setDouble(2, nuevoPorcentaje);

                    int empresasAjustadas = ps.executeUpdate();

                    if (empresasAjustadas > 0) {
                        System.out.println("Listo" + empresasAjustadas
                                + " empresas ajustadas automáticamente a "
                                + nuevoPorcentaje + "%");
                    }
                }
            }

            conn.commit();
            System.out.println("Comisión global actualizada a " + nuevoPorcentaje + "%");
            return true;

        } catch (SQLException e) {
            System.err.println("Error al actualizar comisión global: " + e.getMessage());

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
    
    //Obtiene historial de comisiones globales
    public List<Map<String, Object>> obtenerHistorialComisionGlobal() {
        List<Map<String, Object>> historial = new ArrayList<>();

        String sql = "SELECT id_comision, porcentaje, fecha_inicio "
                + "FROM comision_global ORDER BY fecha_inicio DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> comision = new HashMap<>();
                comision.put("idComision", rs.getInt("id_comision"));
                comision.put("porcentaje", rs.getDouble("porcentaje"));
                comision.put("fechaInicio", rs.getDate("fecha_inicio").toString());

                historial.add(comision);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener historial: " + e.getMessage());
        }

        return historial;
    }

}
