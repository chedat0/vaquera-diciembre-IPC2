/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.google.gson.Gson;
import daos.ImagenDAO;
import dtos.ImagenDTO;
import dtos.VerificadorDTO;
import com.mycompany.vaqueras_ipc2.modelo.Imagen;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jeffm
 */

@WebServlet("/imagenes/*")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,        // 10MB
    maxRequestSize = 1024 * 1024 * 50      // 50MB
)
public class ImagenServlet extends HttpServlet{
    
    private ImagenDAO imagenDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        imagenDAO = new ImagenDAO();
        gson = new Gson();
        System.out.println("ImagenServlet inicializado");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("GET Imagen - Path: " + pathInfo);
        
        if (pathInfo == null || pathInfo.equals("/")) {
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Debe especificar el tipo y ID de imagen");
            return;
        }

        String[] pathParts = pathInfo.split("/");
        
        try {
            if (pathParts.length >= 3 && pathParts[1].equals("juego")) {
                if (pathParts.length == 4 && pathParts[3].equals("portada")) {
                    // /imagenes/juego/idjuego/portada
                    int idJuego = Integer.parseInt(pathParts[2]);
                    enviarPortadaJuego(response, idJuego);
                    
                } else if (pathParts.length == 4 && pathParts[3].equals("lista")) {
                    // /imagenes/juego/idJuego/lista (devuelve JSON con info de imágenes)
                    int idJuego = Integer.parseInt(pathParts[2]);
                    listarImagenesJuego(request, response, idJuego);
                    
                } else if (pathParts.length == 3) {
                    // /imagenes/juego/idImagen
                    int idImagen = Integer.parseInt(pathParts[2]);
                    enviarImagenJuego(response, idImagen);
                }
                
            } else if (pathParts.length >= 4 && pathParts[1].equals("usuario") 
                       && pathParts[3].equals("avatar")) {
                // /imagenes/usuario/idUsuario/avatar
                int idUsuario = Integer.parseInt(pathParts[2]);
                enviarAvatarUsuario(response, idUsuario);
                
            } else if (pathParts.length >= 3 && pathParts[1].equals("banner")) {
                // /imagenes/banner/idBanner
                int idBanner = Integer.parseInt(pathParts[2]);
                enviarImagenBanner(response, idBanner);
                
            } else {
                enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
            }
            
        } catch (NumberFormatException e) {
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                "ID debe ser un número válido");
        }
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("POST Imagen - Path: " + pathInfo);
        
        if (pathInfo == null || pathInfo.equals("/")) {
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Debe especificar el tipo de imagen a subir");
            return;
        }

        String[] pathParts = pathInfo.split("/");
        
        try {
            if (pathParts.length >= 3 && pathParts[1].equals("juego")) {
                // /imagenes/juego/idJuego
                int idJuego = Integer.parseInt(pathParts[2]);
                subirImagenJuego(request, response, idJuego);
                
            } else if (pathParts.length >= 4 && pathParts[1].equals("usuario") 
                       && pathParts[3].equals("avatar")) {
                // /imagenes/usuario/idUsuario/avatar
                int idUsuario = Integer.parseInt(pathParts[2]);
                subirAvatarUsuario(request, response, idUsuario);
                
            } else if (pathParts.length >= 3 && pathParts[1].equals("banner")) {
                // /imagenes/banner/idBanner
                int idBanner = Integer.parseInt(pathParts[2]);
                subirImagenBanner(request, response, idBanner);
                
            } else {
                enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
            }
            
        } catch (NumberFormatException e) {
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                "ID debe ser un número válido");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("PUT Imagen - Path: " + pathInfo);
        
        if (pathInfo == null) {
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Debe especificar la ruta completa");
            return;
        }

        String[] pathParts = pathInfo.split("/");
        
        try {
            if (pathParts.length == 5 && pathParts[1].equals("juego") 
                && pathParts[3].equals("portada")) {
                // /imagenes/juego/idJuego/portada/idImagen
                int idJuego = Integer.parseInt(pathParts[2]);
                int idImagen = Integer.parseInt(pathParts[4]);
                
                boolean exito = imagenDAO.establecerPortadaJuego(idJuego, idImagen);
                
                if (exito) {
                    enviarRespuestaJSON(response, HttpServletResponse.SC_OK, 
                        new VerificadorDTO(true, "Portada actualizada correctamente", null));
                } else {
                    enviarRespuestaJSON(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        new VerificadorDTO(false, "Error al actualizar portada", null));
                }
            } else {
                enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
            }
            
        } catch (NumberFormatException e) {
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }

   
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("DELETE Imagen - Path: " + pathInfo);
        
        if (pathInfo == null) {
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Debe especificar el tipo y ID de imagen");
            return;
        }

        String[] pathParts = pathInfo.split("/");
        boolean exito = false;
        
        try {
            if (pathParts.length >= 3 && pathParts[1].equals("juego")) {
                // /imagenes/juego/idImagen
                int idImagen = Integer.parseInt(pathParts[2]);
                exito = imagenDAO.eliminarImagenJuego(idImagen);
                
            } else if (pathParts.length >= 4 && pathParts[1].equals("usuario") 
                       && pathParts[3].equals("avatar")) {
                // /imagenes/usuario/idUsuario/avatar
                int idUsuario = Integer.parseInt(pathParts[2]);
                exito = imagenDAO.eliminarAvatarUsuario(idUsuario);
                
            } else {
                enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
                return;
            }
            
            if (exito) {
                enviarRespuestaJSON(response, HttpServletResponse.SC_OK, 
                    new VerificadorDTO(true, "Imagen eliminada correctamente", null));
            } else {
                enviarRespuestaJSON(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    new VerificadorDTO(false, "Error al eliminar imagen", null));
            }
            
        } catch (NumberFormatException e) {
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
        
    /**
     * Lista las imágenes de un juego (devuelve JSON con metadata, no los bytes)
     */
    private void listarImagenesJuego(HttpServletRequest request, HttpServletResponse response, 
                                     int idJuego) throws IOException {
        List<Imagen> imagenes = imagenDAO.obtenerImagenesJuego(idJuego);
        List<ImagenDTO> imagenesDTO = new ArrayList<>();
        
        String baseURL = request.getScheme() + "://" + 
                        request.getServerName() + ":" + 
                        request.getServerPort() + 
                        request.getContextPath() + "/imagenes/juego/";
        
        for (Imagen img : imagenes) {
            ImagenDTO dto = new ImagenDTO(
                img.getIdImagen(),
                img.getNombreArchivo(),
                img.getTipoMime(),
                img.getTamanoBytes(),
                img.isEsPortada(),
                baseURL + img.getIdImagen()
            );
            imagenesDTO.add(dto);
        }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(imagenesDTO));
        
        System.out.println("Lista de imágenes enviada para juego " + idJuego);
    }
    
    private void subirImagenJuego(HttpServletRequest request, HttpServletResponse response, 
                                  int idJuego) throws ServletException, IOException {
        
        Part filePart = request.getPart("imagen");
        
        if (filePart == null) {
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Debe enviar un archivo con el nombre 'imagen'");
            return;
        }

        // Validar tipo MIME
        String tipoMime = filePart.getContentType();
        if (!esImagenValida(tipoMime)) {
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Solo se permiten imágenes (JPEG, PNG, GIF, WEBP)");
            return;
        }

        String esPortadaParam = request.getParameter("esPortada");
        boolean esPortada = esPortadaParam != null && esPortadaParam.equals("true");

        byte[] imageData = convertirInputStreamABytes(filePart.getInputStream());
        
        // Validar tamaño
        if (imageData.length > 10 * 1024 * 1024) { // 10MB
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                "La imagen no puede superar 10MB");
            return;
        }
        
        String nombreArchivo = obtenerNombreArchivo(filePart);

        Imagen imagen = new Imagen(idJuego, imageData, nombreArchivo, tipoMime, "JUEGO");
        imagen.setEsPortada(esPortada);

        boolean exito = imagenDAO.guardarImagenJuego(idJuego, imagen);

        if (exito) {
            enviarRespuestaJSON(response, HttpServletResponse.SC_CREATED, 
                new VerificadorDTO(true, "Imagen subida correctamente", imagen.getIdImagen()));
        } else {
            enviarRespuestaJSON(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                new VerificadorDTO(false, "Error al subir imagen", null));
        }
    }

    private void subirAvatarUsuario(HttpServletRequest request, HttpServletResponse response, 
                                    int idUsuario) throws ServletException, IOException {
        
        Part filePart = request.getPart("avatar");
        
        if (filePart == null) {
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Debe enviar un archivo con el nombre 'avatar'");
            return;
        }

        // Validar tipo MIME
        String tipoMime = filePart.getContentType();
        if (!esImagenValida(tipoMime)) {
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Solo se permiten imágenes (JPEG, PNG, GIF, WEBP)");
            return;
        }

        byte[] imageData = convertirInputStreamABytes(filePart.getInputStream());
        
        // Validar tamaño (avatares más pequeños)
        if (imageData.length > 5 * 1024 * 1024) { // 5MB
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                "El avatar no puede superar 5MB");
            return;
        }
        
        String nombreArchivo = obtenerNombreArchivo(filePart);

        Imagen imagen = new Imagen(idUsuario, imageData, nombreArchivo, tipoMime, "USUARIO");

        boolean exito = imagenDAO.guardarAvatarUsuario(idUsuario, imagen);

        if (exito) {
            enviarRespuestaJSON(response, HttpServletResponse.SC_OK, 
                new VerificadorDTO(true, "Avatar actualizado correctamente", null));
        } else {
            enviarRespuestaJSON(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                new VerificadorDTO(false, "Error al actualizar avatar", null));
        }
    }

    private void subirImagenBanner(HttpServletRequest request, HttpServletResponse response, 
                                   int idBanner) throws ServletException, IOException {
        
        Part filePart = request.getPart("imagen");
        
        if (filePart == null) {
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Debe enviar un archivo con el nombre 'imagen'");
            return;
        }

        String tipoMime = filePart.getContentType();
        if (!esImagenValida(tipoMime)) {
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Solo se permiten imágenes (JPEG, PNG, GIF, WEBP)");
            return;
        }

        byte[] imageData = convertirInputStreamABytes(filePart.getInputStream());
        
        if (imageData.length > 10 * 1024 * 1024) { // 10MB
            enviarErrorJSON(response, HttpServletResponse.SC_BAD_REQUEST, 
                "La imagen no puede superar 10MB");
            return;
        }
        
        String nombreArchivo = obtenerNombreArchivo(filePart);

        Imagen imagen = new Imagen(idBanner, imageData, nombreArchivo, tipoMime, "BANNER");

        boolean exito = imagenDAO.guardarImagenBanner(idBanner, imagen);

        if (exito) {
            enviarRespuestaJSON(response, HttpServletResponse.SC_OK, 
                new VerificadorDTO(true, "Imagen de banner actualizada correctamente", null));
        } else {
            enviarRespuestaJSON(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                new VerificadorDTO(false, "Error al actualizar imagen de banner", null));
        }
    }

    private void enviarImagenJuego(HttpServletResponse response, int idImagen) throws IOException {
        Imagen imagen = imagenDAO.obtenerImagenJuego(idImagen);
        enviarImagen(response, imagen);
    }

    private void enviarPortadaJuego(HttpServletResponse response, int idJuego) throws IOException {
        Imagen imagen = imagenDAO.obtenerPortadaJuego(idJuego);
        enviarImagen(response, imagen);
    }

    private void enviarAvatarUsuario(HttpServletResponse response, int idUsuario) throws IOException {
        Imagen imagen = imagenDAO.obtenerAvatarUsuario(idUsuario);
        enviarImagen(response, imagen);
    }

    private void enviarImagenBanner(HttpServletResponse response, int idBanner) throws IOException {
        Imagen imagen = imagenDAO.obtenerImagenBanner(idBanner);
        enviarImagen(response, imagen);
    }

    /**
     * Envía la imagen como bytes (no JSON)
     */
    private void enviarImagen(HttpServletResponse response, Imagen imagen) throws IOException {
        if (imagen == null || imagen.getDatos() == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Imagen no encontrada");
            return;
        }

        response.setContentType(imagen.getTipoMime());
        response.setContentLength(imagen.getTamanoBytes());
        response.setHeader("Content-Disposition", 
            "inline; filename=\"" + imagen.getNombreArchivo() + "\"");
        response.setHeader("Cache-Control", "public, max-age=31536000"); // Cache 1 año

        try (OutputStream out = response.getOutputStream()) {
            out.write(imagen.getDatos());
            out.flush();
        }
        
        System.out.println("Imagen enviada: " + imagen.getNombreArchivo());
    }

    //Convierte InputStream a byte array
    private byte[] convertirInputStreamABytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }

    //Obtiene el nombre del archivo desde el header
    private String obtenerNombreArchivo(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        
        return "imagen_" + System.currentTimeMillis();
    }

    //Valida el tipo de extension del archivo
    private boolean esImagenValida(String tipoMime) {
        return tipoMime != null && (
            tipoMime.equals("image/jpeg") ||
            tipoMime.equals("image/jpg") ||
            tipoMime.equals("image/png") ||
            tipoMime.equals("image/gif") ||
            tipoMime.equals("image/webp")
        );
    }

    //Envia respuesta Json
    private void enviarRespuestaJSON(HttpServletResponse response, int statusCode, 
                                     VerificadorDTO respuesta) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);
        response.getWriter().write(gson.toJson(respuesta));
    }

    //Envia error en formato Json
    private void enviarErrorJSON(HttpServletResponse response, int statusCode, 
                                 String mensaje) throws IOException {
        enviarRespuestaJSON(response, statusCode, new VerificadorDTO(false, mensaje, null));
    }
}
