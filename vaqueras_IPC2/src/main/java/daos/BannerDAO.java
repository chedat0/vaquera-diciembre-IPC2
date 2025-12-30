/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.Banner;
import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author jeffm
 */
public class BannerDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;    
    
    public BannerDAO() {
        conn = connMySQL.conectar();
    }
    
    //Crea un nuevo banner
    public boolean crearBanner(Banner banner) {
        // Verificar que la posición no esté ocupada
        if (existePosicion(banner.getPosicion())) {
            System.err.println(" La posición " + banner.getPosicion() + " ya está ocupada");
            return false;
        }
        
        String sql = "INSERT INTO banner (id_juego, posicion, activo, fecha_inicio, fecha_fin) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, banner.getIdJuego());
            ps.setInt(2, banner.getPosicion());
            ps.setBoolean(3, banner.isActivo());
            ps.setDate(4, banner.getFechaInicio() != null ? 
                Date.valueOf(banner.getFechaInicio()) : null);
            ps.setDate(5, banner.getFechaFin() != null ? 
                Date.valueOf(banner.getFechaFin()) : null);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    banner.setIdBanner(rs.getInt(1));
                }
                
                System.out.println("Banner creado en posición " + banner.getPosicion());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println(" Error al crear banner: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    //Obtiene banners activos ordenados segun la posicion que tengan
    public List<Banner> obtenerBannersActivos() {
        List<Banner> banners = new ArrayList<>();
        
        String sql = "SELECT b.id_banner, b.id_juego, b.posicion, b.activo, " +
                    "b.fecha_inicio, b.fecha_fin, j.titulo, j.descripcion " +
                    "FROM banner b " +
                    "INNER JOIN juego j ON b.id_juego = j.id_juego " +
                    "WHERE b.activo = true " +
                    "ORDER BY b.posicion";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Banner b = new Banner();
                b.setIdBanner(rs.getInt("id_banner"));
                b.setIdJuego(rs.getInt("id_juego"));
                b.setPosicion(rs.getInt("posicion"));
                b.setActivo(rs.getBoolean("activo"));
                
                Date fechaInicio = rs.getDate("fecha_inicio");
                if (fechaInicio != null) {
                    b.setFechaInicio(fechaInicio.toLocalDate());
                }
                
                Date fechaFin = rs.getDate("fecha_fin");
                if (fechaFin != null) {
                    b.setFechaFin(fechaFin.toLocalDate());
                }
                
                b.setTituloJuego(rs.getString("titulo"));
                b.setDescripcionJuego(rs.getString("descripcion"));
                
                banners.add(b);
            }
            
            System.out.println("Obtenidos " + banners.size() + " banners activos");
            
        } catch (SQLException e) {
            System.err.println("Error al obtener banners: " + e.getMessage());
        }
        
        return banners;
    }
    
    //Obtiene banners activos e inactivos
    public List<Banner> obtenerTodosBanners() {
        List<Banner> banners = new ArrayList<>();
        
        String sql = "SELECT b.id_banner, b.id_juego, b.posicion, b.activo, " +
                    "b.fecha_inicio, b.fecha_fin, j.titulo, j.descripcion " +
                    "FROM banner b " +
                    "INNER JOIN juego j ON b.id_juego = j.id_juego " +
                    "ORDER BY b.posicion";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Banner b = new Banner();
                b.setIdBanner(rs.getInt("id_banner"));
                b.setIdJuego(rs.getInt("id_juego"));
                b.setPosicion(rs.getInt("posicion"));
                b.setActivo(rs.getBoolean("activo"));
                
                Date fechaInicio = rs.getDate("fecha_inicio");
                if (fechaInicio != null) {
                    b.setFechaInicio(fechaInicio.toLocalDate());
                }
                
                Date fechaFin = rs.getDate("fecha_fin");
                if (fechaFin != null) {
                    b.setFechaFin(fechaFin.toLocalDate());
                }
                
                b.setTituloJuego(rs.getString("titulo"));
                b.setDescripcionJuego(rs.getString("descripcion"));
                
                banners.add(b);
            }
            
        } catch (SQLException e) {
            System.err.println(" Error al obtener banners: " + e.getMessage());
        }
        
        return banners;
    }
    
    //Obtener banner por medio de id
    public Banner obtenerBannerPorId(int idBanner) {
        String sql = "SELECT b.id_banner, b.id_juego, b.posicion, b.activo, " +
                    "b.fecha_inicio, b.fecha_fin, j.titulo, j.descripcion " +
                    "FROM banner b " +
                    "INNER JOIN juego j ON b.id_juego = j.id_juego " +
                    "WHERE b.id_banner = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBanner);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Banner b = new Banner();
                b.setIdBanner(rs.getInt("id_banner"));
                b.setIdJuego(rs.getInt("id_juego"));
                b.setPosicion(rs.getInt("posicion"));
                b.setActivo(rs.getBoolean("activo"));
                
                Date fechaInicio = rs.getDate("fecha_inicio");
                if (fechaInicio != null) {
                    b.setFechaInicio(fechaInicio.toLocalDate());
                }
                
                Date fechaFin = rs.getDate("fecha_fin");
                if (fechaFin != null) {
                    b.setFechaFin(fechaFin.toLocalDate());
                }
                
                b.setTituloJuego(rs.getString("titulo"));
                b.setDescripcionJuego(rs.getString("descripcion"));
                
                return b;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener banner: " + e.getMessage());
        }
        
        return null;
    }
    
    //Actualiza la posicion de un banner
    public boolean actualizarPosicion(int idBanner, int nuevaPosicion) {
        // Verificar que la posición no esté ocupada por otro banner
        Banner bannerEnPosicion = obtenerBannerPorPosicion(nuevaPosicion);
        
        if (bannerEnPosicion != null && bannerEnPosicion.getIdBanner() != idBanner) {
            System.err.println("La posición " + nuevaPosicion + " ya está ocupada");
            return false;
        }
        
        String sql = "UPDATE banner SET posicion = ? WHERE id_banner = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nuevaPosicion);
            ps.setInt(2, idBanner);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println(" Posición del banner actualizada a " + nuevaPosicion);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar posición: " + e.getMessage());
        }
        
        return false;
    }
    
    //Actualiza es estado activo o inactivo de un banner
    public boolean actualizarEstado(int idBanner, boolean activo) {
        String sql = "UPDATE banner SET activo = ? WHERE id_banner = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, idBanner);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println(" Estado del banner actualizado: " + 
                                 (activo ? "ACTIVO" : "INACTIVO"));
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
        }
        
        return false;
    }
    
    //Actualiza las fechas de inicio/fin de un banner
    public boolean actualizarFechas(int idBanner, LocalDate fechaInicio, LocalDate fechaFin) {
        String sql = "UPDATE banner SET fecha_inicio = ?, fecha_fin = ? WHERE id_banner = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, fechaInicio != null ? Date.valueOf(fechaInicio) : null);
            ps.setDate(2, fechaFin != null ? Date.valueOf(fechaFin) : null);
            ps.setInt(3, idBanner);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Fechas del banner actualizadas");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar fechas: " + e.getMessage());
        }
        
        return false;
    }
    
    //Elimina un banner
    public boolean eliminarBanner(int idBanner) {
        String sql = "DELETE FROM banner WHERE id_banner = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBanner);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println(" Banner eliminado: " + idBanner);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar banner: " + e.getMessage());
        }
        
        return false;
    }
    
    //Verifica la posicion de un banner
    private boolean existePosicion(int posicion) {
        String sql = "SELECT COUNT(*) FROM banner WHERE posicion = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, posicion);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar posición: " + e.getMessage());
        }
        
        return false;
    }
    
    //Obtiene la posicion de un banner
    private Banner obtenerBannerPorPosicion(int posicion) {
        String sql = "SELECT id_banner FROM banner WHERE posicion = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, posicion);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Banner b = new Banner();
                b.setIdBanner(rs.getInt("id_banner"));
                return b;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener banner por posición: " + e.getMessage());
        }
        
        return null;
    }
    
    //Algoritmo de balance 
    /*
        El algoritmo que parece tener menos problemas con las ventas altas o bajas y su calificacion es el promedio bayesiano 
        es el que se adapta más para que el balance no sea tan diferente y no haya tanto problema, lo vimos en estadistica y es acorde a lo 
        que se necesita.
    
    comentarios realizados que hayan hecho una calificacion.
    formula: ((ventas * calificacion promedio) + (comentarios realizados * calificacion promedio global)) / (ventas + comentarios realizados) 
    
    se toman en cuenta 2 datos constantes para simular el modelo de balance 
    ya que el teorema de bayes o el promedio bayesiano se basa en: 
    promedio (modelos) = promedio (predicciones) es una combinacion de modelos y predicciones en base a sus probabilidades posteriores.
    */
    
    public List<Map<String, Object>> obtenerJuegosMejorBalance(int limite) {
        List<Map<String, Object>> juegos = new ArrayList<>();
        
        // Constantes del algoritmo promedio bayesiano
        final double M = 10.0;  // Mínimo de comentarios realizados
        final double C = 3.5;   // Calificación promedio global (en base a prediccion) dato sugerido se puede cambiar xd 
        
        String sql = "SELECT " +
                    "    j.id_juego, " +
                    "    j.titulo, " +
                    "    j.precio, " +
                    "    COUNT(DISTINCT l.id_licencia) as ventas, " +
                    "    COALESCE(AVG(c.calificacion), 0) as calificacion_promedio, " +
                    "    COUNT(DISTINCT c.id_comentario) as total_comentarios, " +
                    "    -- Bayesian Average Score " +
                    "    ((COUNT(DISTINCT l.id_licencia) * COALESCE(AVG(c.calificacion), 0)) + (? * ?)) / " +
                    "    (COUNT(DISTINCT l.id_licencia) + ?) as score_balance " +
                    "FROM juego j " +
                    "LEFT JOIN licencia l ON j.id_juego = l.id_juego " +
                    "LEFT JOIN comentario c ON j.id_juego = c.id_juego " +
                    "WHERE j.venta_activa = true " +
                    "GROUP BY j.id_juego, j.titulo, j.precio " +
                    "ORDER BY score_balance DESC " +
                    "LIMIT ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, M);
            ps.setDouble(2, C);
            ps.setDouble(3, M);
            ps.setInt(4, limite);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> juego = new HashMap<>();
                juego.put("idJuego", rs.getInt("id_juego"));
                juego.put("titulo", rs.getString("titulo"));
                juego.put("precio", rs.getDouble("precio"));
                juego.put("ventas", rs.getInt("ventas"));
                juego.put("calificacionPromedio", 
                    Math.round(rs.getDouble("calificacion_promedio") * 100.0) / 100.0);
                juego.put("totalComentarios", rs.getInt("total_comentarios"));
                juego.put("scoreBalance", 
                    Math.round(rs.getDouble("score_balance") * 100.0) / 100.0);
                
                juegos.add(juego);
            }
            
            System.out.println(" Obtenidos " + juegos.size() + 
                             " juegos con mejor balance");
            
        } catch (SQLException e) {
            System.err.println(" Error al obtener juegos con mejor balance: " + e.getMessage());
        }
        
        return juegos;
    }

}
