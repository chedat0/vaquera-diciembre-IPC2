/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import daos.ComentarioDAO;
import daos.LicenciaDAO;
import daos.RespuestaDAO;
import dtos.ComentarioDTO;
import dtos.RespuestaDTO;
import dtos.VerificadorDTO;
import com.mycompany.vaqueras_ipc2.modelo.Comentario;
import com.mycompany.vaqueras_ipc2.modelo.Respuesta;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 *
 * @author jeffm
 */

@WebServlet ("/comentarios/*")
public class ComentarioServlet extends HttpServlet{
    
    private ComentarioDAO comentarioDAO;
    private RespuestaDAO respuestaDAO;
    private LicenciaDAO licenciaDAO;
    private ObjectMapper objectMapper;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        comentarioDAO = new ComentarioDAO();
        respuestaDAO = new RespuestaDAO();
        licenciaDAO = new LicenciaDAO();               
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());                
        gson = new Gson();        
        System.out.println("ComentarioServlet inicializado");
    }
    
    //Obtener Comentarios
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("GET Comentarios - Path: " + pathInfo);
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar la ruta");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length >= 3 && pathParts[1].equals("juego")) {
                int idJuego = Integer.parseInt(pathParts[2]);
                
                if (pathParts.length >= 4 && pathParts[3].equals("promedio")) {                    
                    obtenerPromedioCalificacion(response, idJuego);
                } else {                    
                    listarComentariosPorJuego(request, response, idJuego);
                }
                
            } else if (pathParts.length >= 3 && pathParts[1].equals("usuario")) {                
                int idUsuario = Integer.parseInt(pathParts[2]);
                listarComentariosPorUsuario(response, idUsuario);
                
            } else if (pathParts.length >= 2) {                
                int idComentario = Integer.parseInt(pathParts[1]);
                obtenerComentarioPorId(response, idComentario);
                
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Crear un comentario
     @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("POST Crear Comentario");
        
        try {
            BufferedReader reader = request.getReader();
            String json = reader.lines().collect(Collectors.joining());
            
            System.out.println("JSON recibido: " + json);
            
            ComentarioDTO dto = objectMapper.readValue(json, ComentarioDTO.class);
            
            // validaciones
            
            if (dto.getIdJuego() == null || dto.getIdJuego() <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El ID del juego es obligatorio");
                return;
            }
            
            if (dto.getIdUsuario() == null || dto.getIdUsuario() <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El ID del usuario es obligatorio");
                return;
            }
            
            if (dto.getContenido() == null || dto.getContenido().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El contenido del comentario es obligatorio");
                return;
            }
            
            if (dto.getCalificacion() == null || dto.getCalificacion() < 1 || dto.getCalificacion() > 5) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "La calificación debe estar entre 1 y 5 estrellas");
                return;
            }
            
            if (dto.getFecha() == null || dto.getFecha().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "La fecha es obligatoria");
                return;
            }
            
            // Usuario debe tener licencia del juego
            if (!licenciaDAO.tieneLicencia(dto.getIdUsuario(), dto.getIdJuego())) {
                enviarError(response, HttpServletResponse.SC_FORBIDDEN, 
                    "Solo los usuarios que poseen el juego pueden comentar y calificar");
                return;
            }
            
            // Crear comentario
            Comentario comentario = new Comentario();
            comentario.setIdJuego(dto.getIdJuego());
            comentario.setIdUsuario(dto.getIdUsuario());
            comentario.setContenido(dto.getContenido().trim());
            comentario.setCalificacion(dto.getCalificacion());
            comentario.setFecha(LocalDate.parse(dto.getFecha()));
            comentario.setVisible(true); // Siempre visible por defecto
            
            boolean exito = comentarioDAO.crearComentario(comentario);
            
            if (exito) {
                ComentarioDTO respuestaDTO = new ComentarioDTO();
                respuestaDTO.setIdComentario(comentario.getIdComentario());
                respuestaDTO.setIdJuego(comentario.getIdJuego());
                respuestaDTO.setIdUsuario(comentario.getIdUsuario());
                respuestaDTO.setContenido(comentario.getContenido());
                respuestaDTO.setCalificacion(comentario.getCalificacion());
                respuestaDTO.setFecha(comentario.getFecha().toString());
                respuestaDTO.setVisible(comentario.isVisible());
                
                enviarRespuesta(response, HttpServletResponse.SC_CREATED, 
                    true, "Comentario creado correctamente", respuestaDTO);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al crear el comentario");
            }
            
        } catch (Exception e) {
            System.err.println("Error en POST Comentario: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al procesar la solicitud: " + e.getMessage());
        }
    }
    
    //Actualizar comentario o la visibilidad de los mismos o de todos los comnetarios de un juego
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("PUT Comentario - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar la ruta");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length >= 4 && pathParts[1].equals("juego") && pathParts[3].equals("visibilidad")) {                
                int idJuego = Integer.parseInt(pathParts[2]);
                cambiarVisibilidadPorJuego(request, response, idJuego);
                
            } else if (pathParts.length >= 3 && pathParts[2].equals("visibilidad")) {                
                int idComentario = Integer.parseInt(pathParts[1]);
                cambiarVisibilidad(request, response, idComentario);
                
            } else if (pathParts.length >= 2) {                
                int idComentario = Integer.parseInt(pathParts[1]);
                actualizarComentario(request, response, idComentario);
                
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Eliminar comentario
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("DELETE Comentario - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar el comentario");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length < 2) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
                return;
            }
            
            int idComentario = Integer.parseInt(pathParts[1]);
            
            // Primero eliminar respuestas
            respuestaDAO.eliminarRespuestasPorComentario(idComentario);
            
            // Luego eliminar comentario
            boolean exito = comentarioDAO.eliminarComentario(idComentario);
            
            if (exito) {
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Comentario eliminado correctamente", null);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al eliminar el comentario");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Listar comentarios por juego
    private void listarComentariosPorJuego(HttpServletRequest request, HttpServletResponse response, 
                                           int idJuego) throws IOException {
        
        // soloVisibles (por defecto true)
        String soloVisiblesParam = request.getParameter("soloVisibles");
        boolean soloVisibles = soloVisiblesParam == null || soloVisiblesParam.equals("true");
        
        List<Comentario> comentarios = comentarioDAO.obtenerComentariosPorJuego(idJuego, soloVisibles);
        
        List<ComentarioDTO> comentariosDTO = new ArrayList<>();
        
        for (Comentario c : comentarios) {
            ComentarioDTO dto = convertirADTO(c);
            
            // Obtener respuestas
            List<Respuesta> respuestas = respuestaDAO.obtenerRespuestasPorComentario(c.getIdComentario());
            List<RespuestaDTO> respuestasDTO = new ArrayList<>();
            
            for (Respuesta r : respuestas) {
                RespuestaDTO rDto = new RespuestaDTO();
                rDto.setIdRespuesta(r.getIdRespuesta());
                rDto.setIdComentario(r.getIdComentario());
                rDto.setIdUsuario(r.getIdUsuario());
                rDto.setContenido(r.getContenido());
                rDto.setFecha(r.getFecha().toString());
                rDto.setNombreUsuario(r.getNombreUsuario());
                
                respuestasDTO.add(rDto);
            }
            
            dto.setRespuestas(respuestasDTO);
            comentariosDTO.add(dto);
        }
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Comentarios obtenidos correctamente", comentariosDTO);
    }
    
    //Listar comentarios por usuario
    private void listarComentariosPorUsuario(HttpServletResponse response, int idUsuario) 
            throws IOException {
        
        List<Comentario> comentarios = comentarioDAO.obtenerComentariosPorUsuario(idUsuario);
        
        List<ComentarioDTO> comentariosDTO = new ArrayList<>();
        
        for (Comentario c : comentarios) {
            comentariosDTO.add(convertirADTO(c));
        }
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Comentarios del usuario obtenidos", comentariosDTO);
    }
    
    //Obtener comentarios por medio de id
    private void obtenerComentarioPorId(HttpServletResponse response, int idComentario) 
            throws IOException {
        
        Comentario comentario = comentarioDAO.obtenerComentarioPorId(idComentario);
        
        if (comentario == null) {
            enviarError(response, HttpServletResponse.SC_NOT_FOUND, 
                "Comentario no encontrado");
            return;
        }
        
        ComentarioDTO dto = convertirADTO(comentario);
        
        // Obtener respuestas
        List<Respuesta> respuestas = respuestaDAO.obtenerRespuestasPorComentario(idComentario);
        List<RespuestaDTO> respuestasDTO = new ArrayList<>();
        
        for (Respuesta r : respuestas) {
            RespuestaDTO rDto = new RespuestaDTO();
            rDto.setIdRespuesta(r.getIdRespuesta());
            rDto.setIdComentario(r.getIdComentario());
            rDto.setIdUsuario(r.getIdUsuario());
            rDto.setContenido(r.getContenido());
            rDto.setFecha(r.getFecha().toString());
            rDto.setNombreUsuario(r.getNombreUsuario());
            
            respuestasDTO.add(rDto);
        }
        
        dto.setRespuestas(respuestasDTO);
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Comentario obtenido correctamente", dto);
    }
    
    
    //Obtener el promedio de las calificaciones realizadas
    private void obtenerPromedioCalificacion(HttpServletResponse response, int idJuego) 
            throws IOException {
        
        double promedio = comentarioDAO.calcularPromedioCalificacion(idJuego);
        int totalComentarios = comentarioDAO.contarComentarios(idJuego);
        
        Map<String, Object> datos = new HashMap<>();
        datos.put("idJuego", idJuego);
        datos.put("promedioCalificacion", Math.round(promedio * 100.0) / 100.0);
        datos.put("totalComentarios", totalComentarios);
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Calificación promedio calculada", datos);
    }
    
    //Actualizar comentario
     private void actualizarComentario(HttpServletRequest request, HttpServletResponse response, 
                                     int idComentario) throws IOException {
        
        BufferedReader reader = request.getReader();
        String json = reader.lines().collect(Collectors.joining());
        
        ComentarioDTO dto = gson.fromJson(json, ComentarioDTO.class);
        
        if (dto.getContenido() == null || dto.getContenido().trim().isEmpty()) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "El contenido es obligatorio");
            return;
        }
        
        boolean exito = comentarioDAO.actualizarComentario(idComentario, dto.getContenido().trim());
        
        if (exito) {
            enviarRespuesta(response, HttpServletResponse.SC_OK, 
                true, "Comentario actualizado correctamente", null);
        } else {
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al actualizar el comentario");
        }
    }
     
    //Cambia la visibilidad de un comentario
     private void cambiarVisibilidad(HttpServletRequest request, HttpServletResponse response, 
                                   int idComentario) throws IOException {
        
        BufferedReader reader = request.getReader();
        String json = reader.lines().collect(Collectors.joining());
        
        ComentarioDTO dto = gson.fromJson(json, ComentarioDTO.class);
        
        if (dto.getVisible() == null) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "El campo 'visible' es obligatorio");
            return;
        }
        
        boolean exito = comentarioDAO.actualizarVisibilidad(idComentario, dto.getVisible());
        
        if (exito) {
            enviarRespuesta(response, HttpServletResponse.SC_OK, 
                true, "Visibilidad actualizada", null);
        } else {
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al actualizar visibilidad");
        }
    }
     
     //Cambia la visibilidad de todos los comentarios de un juego
     private void cambiarVisibilidadPorJuego(HttpServletRequest request, HttpServletResponse response, 
                                           int idJuego) throws IOException {
        
        BufferedReader reader = request.getReader();
        String json = reader.lines().collect(Collectors.joining());
        
        ComentarioDTO dto = gson.fromJson(json, ComentarioDTO.class);
        
        if (dto.getVisible() == null) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "El campo 'visible' es obligatorio");
            return;
        }
        
        boolean exito = comentarioDAO.actualizarVisibilidadPorJuego(idJuego, dto.getVisible());
        
        if (exito) {
            enviarRespuesta(response, HttpServletResponse.SC_OK, 
                true, "Visibilidad actualizada para todos los comentarios del juego", null);
        } else {
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al actualizar visibilidad");
        }
    }
     
    private ComentarioDTO convertirADTO(Comentario c) {
        ComentarioDTO dto = new ComentarioDTO();
        dto.setIdComentario(c.getIdComentario());
        dto.setIdJuego(c.getIdJuego());
        dto.setIdUsuario(c.getIdUsuario());
        dto.setContenido(c.getContenido());
        dto.setCalificacion(c.getCalificacion());
        dto.setFecha(c.getFecha().toString());
        dto.setVisible(c.isVisible());
        dto.setNombreUsuario(c.getNombreUsuario());
        dto.setTituloJuego(c.getTituloJuego());
        dto.setCantidadRespuestas(c.getCantidadRespuestas());
        
        return dto;
    }
    
    private void enviarRespuesta(HttpServletResponse response, int statusCode, 
                                boolean success, String mensaje, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);
        
        VerificadorDTO respuesta = new VerificadorDTO(success, mensaje, data);
        response.getWriter().write(gson.toJson(respuesta));
    }
    
    private void enviarError(HttpServletResponse response, int statusCode, 
                            String mensaje) throws IOException {
        enviarRespuesta(response, statusCode, false, mensaje, null);
    }
}
