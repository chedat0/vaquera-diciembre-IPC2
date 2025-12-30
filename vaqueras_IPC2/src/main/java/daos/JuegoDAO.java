/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.Juego;
import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jeffm
 */
public class JuegoDAO {
    
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = connMySQL.conectar();
    // Listar todos los juegos activos
    public List<Juego> listarTodos() {
        List<Juego> juegos = new ArrayList<>();
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = connMySQL.conectar();
                        
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión a la BD");
                return juegos;
            }
            
            String sql = "SELECT j.*, e.nombre as nombre_empresa " +
                        "FROM juego j " +
                        "LEFT JOIN empresa e ON j.id_empresa = e.id_empresa " +
                        "WHERE j.venta_activa = true " +
                        "ORDER BY j.fecha_lanzamiento DESC";
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Juego juego = mapearJuegoConEmpresa(rs);
                if (juego != null) {
                    juegos.add(juego);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error SQL en listarTodos: " + e.getMessage());
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
        
        return juegos;
    }
    
    //Buscar juego por ID
    public Juego buscarPorId(Integer idJuego) {
        if (idJuego == null || idJuego <= 0) {
            System.err.println("Error: idJuego inválido");
            return null;
        }
        
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = connMySQL.conectar();
                        
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión a la BD");
                return null;
            }
            
            String sql = "SELECT j.*, e.nombre as nombre_empresa " +
                        "FROM juego j " +
                        "LEFT JOIN empresa e ON j.id_empresa = e.id_empresa " +
                        "WHERE j.id_juego = ?";
            
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idJuego);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearJuegoConEmpresa(rs);
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
    
    //Lista de juegos por cada empresa
    public List<Juego> listarPorEmpresa(Integer idEmpresa) {
        List<Juego> juegos = new ArrayList<>();
        
        if (idEmpresa == null || idEmpresa <= 0) {
            System.err.println("Error: idEmpresa inválido");
            return juegos;
        }
        
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = connMySQL.conectar();
                        
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión a la BD");
                return juegos;
            }
            
            String sql = "SELECT * FROM juego WHERE id_empresa = ? " +
                        "ORDER BY fecha_lanzamiento DESC";
            
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idEmpresa);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Juego juego = mapearJuego(rs);
                if (juego != null) {
                    juegos.add(juego);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error SQL en listarPorEmpresa: " + e.getMessage());
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
        
        return juegos;
    }
    
    //Crea nuevo juego
    public boolean crear(Juego juego) {
        // Validaciones
        if (juego == null) {
            System.err.println("Error: juego no puede ser null");
            return false;
        }
        
        if (juego.getIdEmpresa() == null || juego.getTitulo() == null || 
            juego.getTitulo().trim().isEmpty()) {
            System.err.println("Error: campos obligatorios no pueden ser null");
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
            
            String sql = "INSERT INTO juego (id_empresa, titulo, descripcion, requisitos_minimos, " +
                        "precio, clasificacion_por_edad, fecha_lanzamiento, venta_activa) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, juego.getIdEmpresa());
            stmt.setString(2, juego.getTitulo());
            
            // Manejo seguro de campos null
            if (juego.getDescripcion() != null) {
                stmt.setString(3, juego.getDescripcion());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            
            if (juego.getRequisitosMinimos() != null) {
                stmt.setString(4, juego.getRequisitosMinimos());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            
            stmt.setDouble(5, juego.getPrecio());
            
            if (juego.getClasificacionPorEdad() != null) {
                stmt.setString(6, juego.getClasificacionPorEdad());
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }
            
            if (juego.getFechaLanzamiento() != null) {
                stmt.setDate(7, Date.valueOf(juego.getFechaLanzamiento()));
            } else {
                stmt.setDate(7, Date.valueOf(LocalDate.now()));
            }
            
            // Valor por defecto para venta_activa
            Boolean ventaActiva = juego.getVentaActiva();
            stmt.setBoolean(8, ventaActiva != null ? ventaActiva : true);
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    juego.setIdJuego(rs.getInt(1));
                }
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error SQL en crear: " + e.getMessage());
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
    
    //Actualiza juego
    public boolean actualizar(Juego juego) {
        // Validaciones
        if (juego == null || juego.getIdJuego() == null) {
            System.err.println("Error: juego o ID no pueden ser null");
            return false;
        }
        StringBuilder sql = new StringBuilder("UPDATE juego SET ");
        List<Object> parametros = new ArrayList<>();

        if (juego.getTitulo() != null) {
            sql.append("titulo = ?, ");
            parametros.add(juego.getTitulo());
        }

        if (juego.getDescripcion() != null) {
            sql.append("descripcion = ?, ");
            parametros.add(juego.getDescripcion());
        }

        if (juego.getPrecio() != null) {
            sql.append("precio = ?, ");
            parametros.add(juego.getPrecio());
        }

        if (juego.getRequisitosMinimos() != null) {
            sql.append("requisitos_minimos = ?, ");
            parametros.add(juego.getRequisitosMinimos());
        }

        if (juego.getClasificacionPorEdad() != null) {
            sql.append("clasificacion_por_edad = ?, ");
            parametros.add(juego.getClasificacionPorEdad());
        }

        if (juego.getFechaLanzamiento() != null) {
            sql.append("fecha_lanzamiento = ?, ");
            parametros.add(Date.valueOf(juego.getFechaLanzamiento()));
        }

        // Remover última coma
        sql.setLength(sql.length() - 2);

        sql.append(" WHERE id_juego = ?");
        parametros.add(juego.getIdJuego());

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Juego actualizado: " + juego.getTitulo());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error SQL en actualizar: " + e.getMessage());
        }

        return false;

    }
    //Suspende la venta de un juego
    public boolean desactivarVenta(Integer idJuego) {
        if (idJuego == null || idJuego <= 0) {
            System.err.println("Error: idJuego inválido");
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
            
            String sql = "UPDATE juego SET venta_activa = false WHERE id_juego = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idJuego);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error SQL en desactivarVenta: " + e.getMessage());
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
    
    //Busqueda de juegos por titulo
    public List<Juego> buscarPorTitulo(String titulo) {
        List<Juego> juegos = new ArrayList<>();
        
        if (titulo == null || titulo.trim().isEmpty()) {
            return juegos;
        }
        
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = connMySQL.conectar();
                        
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión a la BD");
                return juegos;
            }
            
            String sql = "SELECT j.*, e.nombre as nombre_empresa " +
                        "FROM juego j " +
                        "LEFT JOIN empresa e ON j.id_empresa = e.id_empresa " +
                        "WHERE j.titulo LIKE ? AND j.venta_activa = true " +
                        "ORDER BY j.fecha_lanzamiento DESC";
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + titulo + "%");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Juego juego = mapearJuegoConEmpresa(rs);
                if (juego != null) {
                    juegos.add(juego);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error SQL en buscarPorTitulo: " + e.getMessage());
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
        
        return juegos;
    }
    
    //Mapea Result set
    private Juego mapearJuegoConEmpresa(ResultSet rs) {
        try {
            if (rs == null) {
                return null;
            }
            
            Juego juego = new Juego();
            juego.setIdJuego(rs.getInt("id_juego"));
            juego.setIdEmpresa(rs.getInt("id_empresa"));
            juego.setTitulo(rs.getString("titulo"));
            juego.setDescripcion(rs.getString("descripcion"));
            juego.setRequisitosMinimos(rs.getString("requisitos_minimos"));
            juego.setPrecio(rs.getDouble("precio"));
            juego.setClasificacionPorEdad(rs.getString("clasificacion_por_edad"));
            
            // Manejo seguro de fecha
            Date fechaBD = rs.getDate("fecha_lanzamiento");
            if (fechaBD != null) {
                juego.setFechaLanzamiento(fechaBD.toLocalDate());
            }
            
            // Manejo seguro de boolean
            boolean ventaActiva = rs.getBoolean("venta_activa");
            juego.setVentaActiva(rs.wasNull() ? true : ventaActiva);
            
            // Nombre de empresa
            juego.setNombreEmpresa(rs.getString("nombre_empresa"));
            
            return juego;
            
        } catch (SQLException e) {
            System.err.println("Error al mapear juego: " + e.getMessage());
            return null;
        }
    }
    
    //mapeo sin nombre de empresa
    private Juego mapearJuego(ResultSet rs) {
        try {
            if (rs == null) {
                return null;
            }
            
            Juego juego = new Juego();
            juego.setIdJuego(rs.getInt("id_juego"));
            juego.setIdEmpresa(rs.getInt("id_empresa"));
            juego.setTitulo(rs.getString("titulo"));
            juego.setDescripcion(rs.getString("descripcion"));
            juego.setRequisitosMinimos(rs.getString("requisitos_minimos"));
            juego.setPrecio(rs.getDouble("precio"));
            juego.setClasificacionPorEdad(rs.getString("clasificacion_por_edad"));
            
            // Manejo seguro de fecha
            Date fechaBD = rs.getDate("fecha_lanzamiento");
            if (fechaBD != null) {
                juego.setFechaLanzamiento(fechaBD.toLocalDate());
            }
            
            // Manejo seguro de boolean
            boolean ventaActiva = rs.getBoolean("venta_activa");
            juego.setVentaActiva(rs.wasNull() ? true : ventaActiva);
            
            return juego;
            
        } catch (SQLException e) {
            System.err.println("Error al mapear juego: " + e.getMessage());
            return null;
        }
    }
}
