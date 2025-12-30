/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import com.mycompany.vaqueras_ipc2.modelo.Transaccion;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jeffm
 */
public class TransaccionDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;    
    
    public TransaccionDAO() {
        conn = connMySQL.conectar();
    }
    
    //Registra una transaccion
    public boolean registrarTransaccion(Transaccion transaccion) {
        String sql = "INSERT INTO transaccion (id_usuario, monto, tipo, fecha, " +
                    "comision_aplicada, ganancia_empresa, ganancia_plataforma) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, transaccion.getIdUsuario());
            ps.setDouble(2, transaccion.getMonto());
            ps.setString(3, transaccion.getTipo().name());
            ps.setDate(4, Date.valueOf(transaccion.getFecha()));
            
            // Para RECARGA, estos campos son null/0
            if (transaccion.getComisionAplicada() != null) {
                ps.setDouble(5, transaccion.getComisionAplicada());
            } else {
                ps.setNull(5, Types.DECIMAL);
            }
            
            if (transaccion.getGananciaEmpresa() != null) {
                ps.setDouble(6, transaccion.getGananciaEmpresa());
            } else {
                ps.setNull(6, Types.DECIMAL);
            }
            
            if (transaccion.getGananciaPlataforma() != null) {
                ps.setDouble(7, transaccion.getGananciaPlataforma());
            } else {
                ps.setNull(7, Types.DECIMAL);
            }
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    transaccion.setIdTransaccion(rs.getInt(1));
                }
                
                System.out.println("Transacción registrada: " + transaccion.getTipo() + 
                                 " | Usuario: " + transaccion.getIdUsuario() + 
                                 " | Monto: $" + transaccion.getMonto());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al registrar transacción: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    //Obtiene el historial de transacciones de un usuario
    public List<Transaccion> obtenerTransaccionesPorUsuario(int idUsuario) {
        List<Transaccion> transacciones = new ArrayList<>();
        
        String sql = "SELECT * FROM transaccion WHERE id_usuario = ? ORDER BY fecha DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Transaccion t = new Transaccion();
                t.setIdTransaccion(rs.getInt("id_transaccion"));
                t.setIdUsuario(rs.getInt("id_usuario"));
                t.setMonto(rs.getDouble("monto"));
                t.setTipo(Transaccion.TipoTransaccion.valueOf(rs.getString("tipo")));
                t.setFecha(rs.getDate("fecha").toLocalDate());
                
                // Estos campos pueden ser null para RECARGA
                double comision = rs.getDouble("comision_aplicada");
                if (!rs.wasNull()) {
                    t.setComisionAplicada(comision);
                }
                
                double gananciaEmp = rs.getDouble("ganancia_empresa");
                if (!rs.wasNull()) {
                    t.setGananciaEmpresa(gananciaEmp);
                }
                
                double gananciaPla = rs.getDouble("ganancia_plataforma");
                if (!rs.wasNull()) {
                    t.setGananciaPlataforma(gananciaPla);
                }
                
                transacciones.add(t);
            }
            
            System.out.println("Obtenidas " + transacciones.size() + 
                             " transacciones del usuario " + idUsuario);
            
        } catch (SQLException e) {
            System.err.println("Error al obtener transacciones: " + e.getMessage());
        }
        
        return transacciones;
    }
    
    //Obtiene transacciones por tipo
    public List<Transaccion> obtenerTransaccionesPorTipo(int idUsuario, 
                                                         Transaccion.TipoTransaccion tipo) {
        List<Transaccion> transacciones = new ArrayList<>();
        
        String sql = "SELECT * FROM transaccion WHERE id_usuario = ? AND tipo = ? " +
                    "ORDER BY fecha DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, tipo.name());
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Transaccion t = new Transaccion();
                t.setIdTransaccion(rs.getInt("id_transaccion"));
                t.setIdUsuario(rs.getInt("id_usuario"));
                t.setMonto(rs.getDouble("monto"));
                t.setTipo(tipo);
                t.setFecha(rs.getDate("fecha").toLocalDate());
                
                double comision = rs.getDouble("comision_aplicada");
                if (!rs.wasNull()) {
                    t.setComisionAplicada(comision);
                }
                
                double gananciaEmp = rs.getDouble("ganancia_empresa");
                if (!rs.wasNull()) {
                    t.setGananciaEmpresa(gananciaEmp);
                }
                
                double gananciaPla = rs.getDouble("ganancia_plataforma");
                if (!rs.wasNull()) {
                    t.setGananciaPlataforma(gananciaPla);
                }
                
                transacciones.add(t);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener transacciones por tipo: " + e.getMessage());
        }
        
        return transacciones;
    }
    
    //Obtiene transaccion por fecha
    public List<Transaccion> obtenerTransaccionesPorFechas(int idUsuario, 
                                                           LocalDate fechaInicio, 
                                                           LocalDate fechaFin) {
        List<Transaccion> transacciones = new ArrayList<>();
        
        String sql;
        if (idUsuario > 0) {
            sql = "SELECT * FROM transaccion WHERE id_usuario = ? " +
                  "AND fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        } else {
            sql = "SELECT * FROM transaccion WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        }
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            
            if (idUsuario > 0) {
                ps.setInt(paramIndex++, idUsuario);
            }
            
            ps.setDate(paramIndex++, Date.valueOf(fechaInicio));
            ps.setDate(paramIndex, Date.valueOf(fechaFin));
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Transaccion t = new Transaccion();
                t.setIdTransaccion(rs.getInt("id_transaccion"));
                t.setIdUsuario(rs.getInt("id_usuario"));
                t.setMonto(rs.getDouble("monto"));
                t.setTipo(Transaccion.TipoTransaccion.valueOf(rs.getString("tipo")));
                t.setFecha(rs.getDate("fecha").toLocalDate());
                
                double comision = rs.getDouble("comision_aplicada");
                if (!rs.wasNull()) {
                    t.setComisionAplicada(comision);
                }
                
                double gananciaEmp = rs.getDouble("ganancia_empresa");
                if (!rs.wasNull()) {
                    t.setGananciaEmpresa(gananciaEmp);
                }
                
                double gananciaPla = rs.getDouble("ganancia_plataforma");
                if (!rs.wasNull()) {
                    t.setGananciaPlataforma(gananciaPla);
                }
                
                transacciones.add(t);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener transacciones por fechas: " + e.getMessage());
        }
        
        return transacciones;
    }
    
    //Calcula lo gastado en compras que hace un usuario
    public double calcularTotalGastado(int idUsuario) {
        String sql = "SELECT COALESCE(SUM(monto), 0) as total FROM transaccion " +
                    "WHERE id_usuario = ? AND tipo = 'COMPRA'";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al calcular total gastado: " + e.getMessage());
        }
        
        return 0.0;
    }
    
    //Calcula el total recargado por un usuario
    public double calcularTotalRecargado(int idUsuario) {
        String sql = "SELECT COALESCE(SUM(monto), 0) as total FROM transaccion " +
                    "WHERE id_usuario = ? AND tipo = 'RECARGA'";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al calcular total recargado: " + e.getMessage());
        }
        
        return 0.0;
    }

}
