/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.Cartera;
import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import java.sql.*;

/**
 *
 * @author jeffm
 */
public class CarteraDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;    
    
    public CarteraDAO() {
        conn = connMySQL.conectar();
    }
    
    //Crea una cartera al momento de registrar usuario
    public boolean crearCartera(int idUsuario) {
        String sql = "INSERT INTO cartera (id_usuario, saldo) VALUES (?, 0.00)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Cartera creada para usuario " + idUsuario);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al crear cartera: " + e.getMessage());
        }
        
        return false;
    }
    
    //Obtiene el saldo actual 
    public Cartera obtenerCartera(int idUsuario) {
        String sql = "SELECT id_usuario, saldo FROM cartera WHERE id_usuario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Cartera cartera = new Cartera();
                cartera.setIdUsuario(rs.getInt("id_usuario"));
                cartera.setSaldo(rs.getDouble("saldo"));
                
                System.out.println("Cartera obtenida: Usuario " + idUsuario + 
                                 " | Saldo: $" + cartera.getSaldo());
                return cartera;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener cartera: " + e.getMessage());
        }
        
        return null;
    }
    
    //Actualiza el saldo de la cartera
    public boolean actualizarSaldo(int idUsuario, double nuevoSaldo) {
        String sql = "UPDATE cartera SET saldo = ? WHERE id_usuario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, nuevoSaldo);
            ps.setInt(2, idUsuario);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Saldo actualizado: Usuario " + idUsuario + 
                                 " | Nuevo saldo: $" + nuevoSaldo);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar saldo: " + e.getMessage());
        }
        
        return false;
    }
    
    //Incrementa el saldo de la cartera cuando se realiza una recarga
    public boolean incrementarSaldo(int idUsuario, double monto) {
        String sql = "UPDATE cartera SET saldo = saldo + ? WHERE id_usuario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, monto);
            ps.setInt(2, idUsuario);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Saldo incrementado: Usuario " + idUsuario + 
                                 " | Monto: +$" + monto);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al incrementar saldo: " + e.getMessage());
        }
        
        return false;
    }
    
    //Resta el saldo de la cartera cuando se realiza una compra
    public boolean decrementarSaldo(int idUsuario, double monto) {
        String sql = "UPDATE cartera SET saldo = saldo - ? WHERE id_usuario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, monto);
            ps.setInt(2, idUsuario);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Saldo restado: Usuario " + idUsuario + 
                                 " | Monto: -$" + monto);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al restar saldo: " + e.getMessage());
        }
        
        return false;
    }
    
    //verifica si existe el saldo suficiente
     public boolean tieneSaldoSuficiente(int idUsuario, double montoRequerido) {
        Cartera cartera = obtenerCartera(idUsuario);
        
        if (cartera == null) {
            System.err.println("Cartera no encontrada para usuario " + idUsuario);
            return false;
        }
        
        boolean suficiente = cartera.getSaldo() >= montoRequerido;
        
        if (suficiente) {
            System.out.println("Saldo suficiente: $" + cartera.getSaldo() + 
                             " >= $" + montoRequerido);
        } else {
            System.err.println("Saldo insuficiente: $" + cartera.getSaldo() + 
                             " < $" + montoRequerido);
        }
        
        return suficiente;
    }
     
     //verifica si el usuario tiene cartera
     public boolean existeCartera(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM cartera WHERE id_usuario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar cartera: " + e.getMessage());
        }
        
        return false;
    }
}
