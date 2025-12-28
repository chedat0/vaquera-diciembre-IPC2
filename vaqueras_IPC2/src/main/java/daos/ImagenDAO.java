/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import com.mycompany.vaqueras_ipc2.modelo.Imagen;
import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeffm
 */
public class ImagenDAO {
    ConnectionMySQL connMySQL = new ConnectionMySQL();
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    public ImagenDAO() {
        conn = connMySQL.conectar();
    }
    
     //JUEGOS
    
    //guarda imagen de juego
    public boolean guardarImagenJuego(int idJuego, Imagen imagen) {
        String sql = "INSERT INTO juego_imagen (id_juego, imagen, nombre_archivo, " +
                     "tipo_mime, tamano_bytes, img_portada) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, idJuego);
            ps.setBytes(2, imagen.getDatos());
            ps.setString(3, imagen.getNombreArchivo());
            ps.setString(4, imagen.getTipoMime());
            ps.setInt(5, imagen.getTamanoBytes());
            ps.setBoolean(6, imagen.isEsPortada());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    imagen.setIdImagen(rs.getInt(1));
                }
                System.out.println("Imagen de juego guardada: " + imagen.getNombreArchivo());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al guardar imagen de juego: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    
     // Obtiene una imagen  de juego por ID
     
    public Imagen obtenerImagenJuego(int idImagen) {
        String sql = "SELECT * FROM juego_imagen WHERE id_imagen = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idImagen);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Imagen img = new Imagen();
                img.setIdImagen(rs.getInt("id_imagen"));
                img.setIdEntidad(rs.getInt("id_juego"));
                img.setDatos(rs.getBytes("imagen"));
                img.setNombreArchivo(rs.getString("nombre_archivo"));
                img.setTipoMime(rs.getString("tipo_mime"));
                img.setTamanoBytes(rs.getInt("tamano_bytes"));
                img.setEsPortada(rs.getBoolean("img_portada"));
                img.setTipoEntidad("JUEGO");
                
                System.out.println("Imagen de juego obtenida: " + idImagen);
                return img;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener imagen de juego: " + e.getMessage());
        }
        
        return null;
    }

    //obtiene las imagenes de un juego
    public List<Imagen> obtenerImagenesJuego(int idJuego) {
        List<Imagen> imagenes = new ArrayList<>();
        String sql = "SELECT * FROM juego_imagen WHERE id_juego = ? ORDER BY img_portada DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Imagen img = new Imagen();
                img.setIdImagen(rs.getInt("id_imagen"));
                img.setIdEntidad(rs.getInt("id_juego"));
                img.setDatos(rs.getBytes("imagen"));
                img.setNombreArchivo(rs.getString("nombre_archivo"));
                img.setTipoMime(rs.getString("tipo_mime"));
                img.setTamanoBytes(rs.getInt("tamano_bytes"));
                img.setEsPortada(rs.getBoolean("img_portada"));
                img.setTipoEntidad("JUEGO");
                
                imagenes.add(img);
            }
            
            System.out.println("Obtenidas " + imagenes.size() + " imágenes del juego " + idJuego);
            
        } catch (SQLException e) {
            System.err.println("Error al obtener imágenes de juego: " + e.getMessage());
        }
        
        return imagenes;
    }

    //Obtiene portada de juego
    public Imagen obtenerPortadaJuego(int idJuego) {
        String sql = "SELECT * FROM juego_imagen WHERE id_juego = ? AND img_portada = true LIMIT 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Imagen img = new Imagen();
                img.setIdImagen(rs.getInt("id_imagen"));
                img.setIdEntidad(rs.getInt("id_juego"));
                img.setDatos(rs.getBytes("imagen"));
                img.setNombreArchivo(rs.getString("nombre_archivo"));
                img.setTipoMime(rs.getString("tipo_mime"));
                img.setTamanoBytes(rs.getInt("tamano_bytes"));
                img.setEsPortada(true);
                img.setTipoEntidad("JUEGO");
                
                return img;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener portada de juego: " + e.getMessage());
        }
        
        return null;
    }

    //Establece una imagen como la portada de juego
    public boolean establecerPortadaJuego(int idJuego, int idImagen) {
        conn = connMySQL.conectar();
        
        try {
            conn.setAutoCommit(false);
            
            // 1. Quitar flag de portada a todas las imágenes del juego
            String sql1 = "UPDATE juego_imagen SET img_portada = false WHERE id_juego = ?";
            try (PreparedStatement ps1 = conn.prepareStatement(sql1)) {
                ps1.setInt(1, idJuego);
                ps1.executeUpdate();
            }
            
            // 2. Establecer la imagen seleccionada como portada
            String sql2 = "UPDATE juego_imagen SET img_portada = true " +
                          "WHERE id_imagen = ? AND id_juego = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                ps2.setInt(1, idImagen);
                ps2.setInt(2, idJuego);
                int filasAfectadas = ps2.executeUpdate();
                
                if (filasAfectadas > 0) {
                    conn.commit();
                    System.out.println("Portada establecida para juego " + idJuego);
                    return true;
                }
            }
            
            conn.rollback();
            
        } catch (SQLException e) {
            System.err.println("Error al establecer portada: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }

    //Elimina la imagen de un juego
    public boolean eliminarImagenJuego(int idImagen) {
        String sql = "DELETE FROM juego_imagen WHERE id_imagen = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idImagen);
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Imagen de juego eliminada: " + idImagen);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar imagen de juego: " + e.getMessage());
        }
        
        return false;
    }

    // USUARIOS (AVATARES)
    
    //Guarda o actualiza un avatar
    public boolean guardarAvatarUsuario(int idUsuario, Imagen imagen) {
        String sql = "UPDATE usuario SET avatar = ?, avatar_nombre = ?, " +
                     "avatar_tipo = ? WHERE id_usuario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBytes(1, imagen.getDatos());
            ps.setString(2, imagen.getNombreArchivo());
            ps.setString(3, imagen.getTipoMime());
            ps.setInt(4, idUsuario);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Avatar de usuario guardado: " + idUsuario);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al guardar avatar de usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    //Obtiene el avatar de un usuario
    public Imagen obtenerAvatarUsuario(int idUsuario) {
        String sql = "SELECT avatar, avatar_nombre, avatar_tipo FROM usuario WHERE id_usuario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                byte[] datos = rs.getBytes("avatar");
                
                if (datos != null && datos.length > 0) {
                    Imagen img = new Imagen();
                    img.setIdEntidad(idUsuario);
                    img.setDatos(datos);
                    img.setNombreArchivo(rs.getString("avatar_nombre"));
                    img.setTipoMime(rs.getString("avatar_tipo"));
                    img.setTipoEntidad("USUARIO");
                    
                    System.out.println("Avatar de usuario obtenido: " + idUsuario);
                    return img;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener avatar de usuario: " + e.getMessage());
        }
        
        return null;
    }

    //Elimina el avatar
    public boolean eliminarAvatarUsuario(int idUsuario) {
        String sql = "UPDATE usuario SET avatar = NULL, avatar_nombre = NULL, " +
                     "avatar_tipo = NULL WHERE id_usuario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Avatar de usuario eliminado: " + idUsuario);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar avatar de usuario: " + e.getMessage());
        }
        
        return false;
    }

    //BANNERS 
    
   //Guarda o actualiza la imagen del banner
    public boolean guardarImagenBanner(int idBanner, Imagen imagen) {
        String sql = "UPDATE banner SET imagen = ?, imagen_nombre = ?, " +
                     "imagen_tipo = ? WHERE id_banner = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBytes(1, imagen.getDatos());
            ps.setString(2, imagen.getNombreArchivo());
            ps.setString(3, imagen.getTipoMime());
            ps.setInt(4, idBanner);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Imagen de banner guardada: " + idBanner);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al guardar imagen de banner: " + e.getMessage());
        }
        
        return false;
    }

    //Obtiene la imagen del banner
    public Imagen obtenerImagenBanner(int idBanner) {
        String sql = "SELECT imagen, imagen_nombre, imagen_tipo FROM banner WHERE id_banner = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBanner);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                byte[] datos = rs.getBytes("imagen");
                
                if (datos != null && datos.length > 0) {
                    Imagen img = new Imagen();
                    img.setIdEntidad(idBanner);
                    img.setDatos(datos);
                    img.setNombreArchivo(rs.getString("imagen_nombre"));
                    img.setTipoMime(rs.getString("imagen_tipo"));
                    img.setTipoEntidad("BANNER");
                    
                    return img;
                }
            }
            
        } catch (SQLException e) {
            System.err.println(" Error al obtener imagen de banner: " + e.getMessage());
        }
        
        return null;
    }
}
