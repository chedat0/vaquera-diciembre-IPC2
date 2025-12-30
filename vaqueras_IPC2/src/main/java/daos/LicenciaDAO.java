/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import com.mycompany.vaqueras_ipc2.modelo.Licencia;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jeffm
 */
public class LicenciaDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null; 
    
    public LicenciaDAO() {
        conn = connMySQL.conectar();
    }
    
    //Crea una nueva licencia al comprar el juego
     public boolean crearLicencia(Licencia licencia) {
        String sql = "INSERT INTO licencia (id_usuario, id_juego, fecha_compra) VALUES (?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, licencia.getIdUsuario());
            ps.setInt(2, licencia.getIdJuego());
            ps.setDate(3, Date.valueOf(licencia.getFechaCompra()));
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    licencia.setIdLicencia(rs.getInt(1));
                }
                
                System.out.println("Licencia creada: Usuario " + licencia.getIdUsuario() + 
                                 " | Juego " + licencia.getIdJuego());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al crear licencia: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
     
     //Verifica si un usuario ya tiene la licencia de un juego
     public boolean tieneLicencia(int idUsuario, int idJuego) {
        String sql = "SELECT COUNT(*) FROM licencia WHERE id_usuario = ? AND id_juego = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idJuego);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                boolean tiene = rs.getInt(1) > 0;
                
                if (tiene) {
                    System.out.println("Usuario " + idUsuario + " ya tiene licencia del juego " + idJuego);
                }
                
                return tiene;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar licencia: " + e.getMessage());
        }
        
        return false;
    }
     
     //Obtiene todas las licencias de un usuario
     public List<Licencia> obtenerLicenciasPorUsuario(int idUsuario) {
        List<Licencia> licencias = new ArrayList<>();
        
        String sql = "SELECT l.id_licencia, l.id_usuario, l.id_juego, l.fecha_compra, " +
                    "j.titulo " +
                    "FROM licencia l " +
                    "INNER JOIN juego j ON l.id_juego = j.id_juego " +
                    "WHERE l.id_usuario = ? " +
                    "ORDER BY l.fecha_compra DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Licencia lic = new Licencia();
                lic.setIdLicencia(rs.getInt("id_licencia"));
                lic.setIdUsuario(rs.getInt("id_usuario"));
                lic.setIdJuego(rs.getInt("id_juego"));
                lic.setFechaCompra(rs.getDate("fecha_compra").toLocalDate());
                lic.setTituloJuego(rs.getString("titulo"));
                
                licencias.add(lic);
            }
            
            System.out.println("Obtenidas " + licencias.size() + 
                             " licencias del usuario " + idUsuario);
            
        } catch (SQLException e) {
            System.err.println("Error al obtener licencias: " + e.getMessage());
        }
        
        return licencias;
    }
     
     //Obtiene todas las licencias de un juego especifico
     public List<Licencia> obtenerLicenciasPorJuego(int idJuego) {
        List<Licencia> licencias = new ArrayList<>();
        
        String sql = "SELECT l.id_licencia, l.id_usuario, l.id_juego, l.fecha_compra, " +
                    "u.nickname " +
                    "FROM licencia l " +
                    "INNER JOIN usuario u ON l.id_usuario = u.id_usuario " +
                    "WHERE l.id_juego = ? " +
                    "ORDER BY l.fecha_compra DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Licencia lic = new Licencia();
                lic.setIdLicencia(rs.getInt("id_licencia"));
                lic.setIdUsuario(rs.getInt("id_usuario"));
                lic.setIdJuego(rs.getInt("id_juego"));
                lic.setFechaCompra(rs.getDate("fecha_compra").toLocalDate());
                lic.setNombreUsuario(rs.getString("nickname"));
                
                licencias.add(lic);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener licencias por juego: " + e.getMessage());
        }
        
        return licencias;
    }
     
     //Cuenta las ventas que ha tenido un juego
     public int contarVentasJuego(int idJuego) {
        String sql = "SELECT COUNT(*) FROM licencia WHERE id_juego = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar ventas: " + e.getMessage());
        }
        
        return 0;
    }
     
     //Obtiene la cantidad de juegos que tiene un usuario
     public int contarJuegosUsuario(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM licencia WHERE id_usuario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar juegos del usuario: " + e.getMessage());
        }
        
        return 0;
    }
     
     //Obtiene licencia segun fecha
     public List<Licencia> obtenerLicenciasPorFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Licencia> licencias = new ArrayList<>();
        
        String sql = "SELECT l.id_licencia, l.id_usuario, l.id_juego, l.fecha_compra, " +
                    "j.titulo, u.nickname " +
                    "FROM licencia l " +
                    "INNER JOIN juego j ON l.id_juego = j.id_juego " +
                    "INNER JOIN usuario u ON l.id_usuario = u.id_usuario " +
                    "WHERE l.fecha_compra BETWEEN ? AND ? " +
                    "ORDER BY l.fecha_compra DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Licencia lic = new Licencia();
                lic.setIdLicencia(rs.getInt("id_licencia"));
                lic.setIdUsuario(rs.getInt("id_usuario"));
                lic.setIdJuego(rs.getInt("id_juego"));
                lic.setFechaCompra(rs.getDate("fecha_compra").toLocalDate());
                lic.setTituloJuego(rs.getString("titulo"));
                lic.setNombreUsuario(rs.getString("nickname"));
                
                licencias.add(lic);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener licencias por fechas: " + e.getMessage());
        }
        
        return licencias;
    }

     
}
