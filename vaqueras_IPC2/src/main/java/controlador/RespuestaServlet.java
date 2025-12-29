/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import daos.ComentarioDAO;
import daos.RespuestaDAO;
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
import java.util.List;
import java.util.stream.Collectors;
/**
 *
 * @author jeffm
 */

@WebServlet ("/respuestas/*")
public class RespuestaServlet extends HttpServlet{
    
    private RespuestaDAO respuestaDAO;
    private ComentarioDAO comentarioDAO;
    private ObjectMapper objectMapper;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        respuestaDAO = new RespuestaDAO();
        comentarioDAO = new ComentarioDAO();                
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());                
        gson = new Gson();
        
        System.out.println("RespuestaServlet inicializado");
    }
    
    //Obtener respuesta
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("GET Respuestas - Path: " + pathInfo);
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar la ruta");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length >= 3 && pathParts[1].equals("comentario")) {                
                int idComentario = Integer.parseInt(pathParts[2]);
                listarRespuestasPorComentario(response, idComentario);
                
            } else if (pathParts.length >= 3 && pathParts[1].equals("usuario")) {                
                int idUsuario = Integer.parseInt(pathParts[2]);
                listarRespuestasPorUsuario(response, idUsuario);
                
            } else if (pathParts.length >= 2) {               
                int idRespuesta = Integer.parseInt(pathParts[1]);
                obtenerRespuestaPorId(response, idRespuesta);
                
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Crear la respuesta a un comentario
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("POST Crear Respuesta");
        
        try {
            BufferedReader reader = request.getReader();
            String json = reader.lines().collect(Collectors.joining());
            
            System.out.println("JSON recibido: " + json);
            
            RespuestaDTO dto = objectMapper.readValue(json, RespuestaDTO.class);
            
            // validaciones
            
            if (dto.getIdComentario() == null || dto.getIdComentario() <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El ID del comentario es obligatorio");
                return;
            }
            
            if (dto.getIdUsuario() == null || dto.getIdUsuario() <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El ID del usuario es obligatorio");
                return;
            }
            
            if (dto.getContenido() == null || dto.getContenido().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El contenido de la respuesta es obligatorio");
                return;
            }
            
            if (dto.getFecha() == null || dto.getFecha().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "La fecha es obligatoria");
                return;
            }
            
            // Validar que el comentario existe
            Comentario comentario = comentarioDAO.obtenerComentarioPorId(dto.getIdComentario());
            
            if (comentario == null) {
                enviarError(response, HttpServletResponse.SC_NOT_FOUND, 
                    "El comentario no existe");
                return;
            }
            
            // Crear respuesta
            Respuesta respuesta = new Respuesta();
            respuesta.setIdComentario(dto.getIdComentario());
            respuesta.setIdUsuario(dto.getIdUsuario());
            respuesta.setContenido(dto.getContenido().trim());
            respuesta.setFecha(LocalDate.parse(dto.getFecha()));
            
            boolean exito = respuestaDAO.crearRespuesta(respuesta);
            
            if (exito) {
                RespuestaDTO respuestaDTO = new RespuestaDTO();
                respuestaDTO.setIdRespuesta(respuesta.getIdRespuesta());
                respuestaDTO.setIdComentario(respuesta.getIdComentario());
                respuestaDTO.setIdUsuario(respuesta.getIdUsuario());
                respuestaDTO.setContenido(respuesta.getContenido());
                respuestaDTO.setFecha(respuesta.getFecha().toString());
                
                enviarRespuesta(response, HttpServletResponse.SC_CREATED, 
                    true, "Respuesta creada correctamente", respuestaDTO);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al crear la respuesta");
            }
            
        } catch (Exception e) {
            System.err.println("Error en POST Respuesta: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al procesar la solicitud: " + e.getMessage());
        }
    }
    
    //Actualizar respuesta
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("PUT Respuesta - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar la respuesta");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length < 2) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
                return;
            }
            
            int idRespuesta = Integer.parseInt(pathParts[1]);
            
            BufferedReader reader = request.getReader();
            String json = reader.lines().collect(Collectors.joining());
            
            RespuestaDTO dto = gson.fromJson(json, RespuestaDTO.class);
            
            if (dto.getContenido() == null || dto.getContenido().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El contenido es obligatorio");
                return;
            }
            
            boolean exito = respuestaDAO.actualizarRespuesta(idRespuesta, dto.getContenido().trim());
            
            if (exito) {
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Respuesta actualizada correctamente", null);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al actualizar la respuesta");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Eliminar respuesta
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println(" DELETE Respuesta - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar la respuesta");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length < 2) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
                return;
            }
            
            int idRespuesta = Integer.parseInt(pathParts[1]);
            
            boolean exito = respuestaDAO.eliminarRespuesta(idRespuesta);
            
            if (exito) {
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Respuesta eliminada correctamente", null);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al eliminar la respuesta");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Lista de respuestas segun el comentario
     private void listarRespuestasPorComentario(HttpServletResponse response, int idComentario) 
            throws IOException {
        
        List<Respuesta> respuestas = respuestaDAO.obtenerRespuestasPorComentario(idComentario);
        
        List<RespuestaDTO> respuestasDTO = convertirADTO(respuestas);
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Respuestas obtenidas correctamente", respuestasDTO);
    }

     //Lista de respuesta segun el usuario
     private void listarRespuestasPorUsuario(HttpServletResponse response, int idUsuario) 
            throws IOException {
        
        List<Respuesta> respuestas = respuestaDAO.obtenerRespuestasPorUsuario(idUsuario);
        
        List<RespuestaDTO> respuestasDTO = convertirADTO(respuestas);
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Respuestas del usuario obtenidas", respuestasDTO);
    }
     
    //Obtener una respuesta por medio de id
      private void obtenerRespuestaPorId(HttpServletResponse response, int idRespuesta) 
            throws IOException {
        
        Respuesta respuesta = respuestaDAO.obtenerRespuestaPorId(idRespuesta);
        
        if (respuesta == null) {
            enviarError(response, HttpServletResponse.SC_NOT_FOUND, 
                "Respuesta no encontrada");
            return;
        }
        
        RespuestaDTO dto = convertirADTO(respuesta);
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Respuesta obtenida correctamente", dto);
    }

    private List<RespuestaDTO> convertirADTO(List<Respuesta> respuestas) {
        List<RespuestaDTO> dtos = new ArrayList<>();
        
        for (Respuesta r : respuestas) {
            dtos.add(convertirADTO(r));
        }
        
        return dtos;
    }
    
    private RespuestaDTO convertirADTO(Respuesta r) {
        RespuestaDTO dto = new RespuestaDTO();
        dto.setIdRespuesta(r.getIdRespuesta());
        dto.setIdComentario(r.getIdComentario());
        dto.setIdUsuario(r.getIdUsuario());
        dto.setContenido(r.getContenido());
        dto.setFecha(r.getFecha().toString());
        dto.setNombreUsuario(r.getNombreUsuario());
        
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
