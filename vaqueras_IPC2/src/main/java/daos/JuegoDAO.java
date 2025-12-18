/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.Juego;
import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jeffm
 */
public class JuegoDAO {
    // Listar todos los juegos activos
    public List<Juego> listarTodos() {
        List<Juego> juegos = new ArrayList<>();
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "SELECT * FROM juego WHERE venta_activa = true ORDER BY fecha_lanzamiento DESC";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                juegos.add(mapearJuego(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return juegos;
    }
    
    // Buscar juego por ID
    public Juego buscarPorId(Integer idJuego) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "SELECT * FROM juego WHERE id_juego = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idJuego);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearJuego(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Listar juegos por empresa
    public List<Juego> listarPorEmpresa(Integer idEmpresa) {
        List<Juego> juegos = new ArrayList<>();
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "SELECT * FROM juego WHERE id_empresa = ? ORDER BY fecha_lanzamiento DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idEmpresa);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                juegos.add(mapearJuego(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return juegos;
    }
    
    // Crear juego
    public boolean crear(Juego juego) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "INSERT INTO juego (id_empresa, titulo, descripcion, requisitos_minimos, " +
                     "precio, clasificacion_por_edad, fecha_lanzamiento, venta_activa) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, juego.getIdEmpresa());
            stmt.setString(2, juego.getTitulo());
            stmt.setString(3, juego.getDescripcion());
            stmt.setString(4, juego.getRequisitosMinimos());
            stmt.setDouble(5, juego.getPrecio());
            stmt.setString(6, juego.getClasificacionPorEdad());
            stmt.setDate(7, Date.valueOf(juego.getFechaLanzamiento()));
            stmt.setBoolean(8, juego.getVentaActiva() != null ? juego.getVentaActiva() : true);
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    juego.setIdJuego(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Actualizar juego
    public boolean actualizar(Juego juego) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "UPDATE juego SET titulo = ?, descripcion = ?, requisitos_minimos = ?, " +
                     "precio = ?, clasificacion_por_edad = ?, fecha_lanzamiento = ?, " +
                     "venta_activa = ? WHERE id_juego = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, juego.getTitulo());
            stmt.setString(2, juego.getDescripcion());
            stmt.setString(3, juego.getRequisitosMinimos());
            stmt.setDouble(4, juego.getPrecio());
            stmt.setString(5, juego.getClasificacionPorEdad());
            stmt.setDate(6, Date.valueOf(juego.getFechaLanzamiento()));
            stmt.setBoolean(7, juego.getVentaActiva());
            stmt.setInt(8, juego.getIdJuego());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Desactivar venta
    public boolean desactivarVenta(Integer idJuego) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        String sql = "UPDATE juego SET venta_activa = false WHERE id_juego = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idJuego);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Buscar juegos por título
    public List<Juego> buscarPorTitulo(String titulo) {
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = connMySQL.conectar();
        List<Juego> juegos = new ArrayList<>();
        String sql = "SELECT * FROM juego WHERE titulo LIKE ? AND venta_activa = true";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + titulo + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                juegos.add(mapearJuego(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return juegos;
    }
    
    // Mapear ResultSet a Juego
    private Juego mapearJuego(ResultSet rs) throws SQLException {
        Juego juego = new Juego();
        juego.setIdJuego(rs.getInt("id_juego"));
        juego.setIdEmpresa(rs.getInt("id_empresa"));
        juego.setTitulo(rs.getString("titulo"));
        juego.setDescripcion(rs.getString("descripcion"));
        juego.setRequisitosMinimos(rs.getString("requisitos_minimos"));
        juego.setPrecio(rs.getDouble("precio"));
        juego.setClasificacionPorEdad(rs.getString("clasificacion_por_edad"));
        juego.setFechaLanzamiento(rs.getDate("fecha_lanzamiento").toLocalDate());
        juego.setVentaActiva(rs.getBoolean("venta_activa"));
        return juego;
    }
}
